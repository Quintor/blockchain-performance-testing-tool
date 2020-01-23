//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package nl.quintor.blockchain.ptt.blockchainproviders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import nl.quintor.blockchain.ptt.api.TransactionError;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.GatewayRuntimeException;
import org.hyperledger.fabric.gateway.Transaction;
import org.hyperledger.fabric.gateway.impl.*;
import org.hyperledger.fabric.gateway.spi.CommitHandlerFactory;
import org.hyperledger.fabric.gateway.spi.Query;
import org.hyperledger.fabric.gateway.spi.QueryHandler;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel.DiscoveryOptions;
import org.hyperledger.fabric.sdk.Channel.NOfEvents;
import org.hyperledger.fabric.sdk.Channel.TransactionOptions;
import org.hyperledger.fabric.sdk.ServiceDiscovery.EndorsementSelector;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.ServiceDiscoveryException;

public final class CustomTransaction implements Transaction {
    private static final Log logger = LogFactory.getLog(CustomTransaction.class);
    private final ContractImpl contract;
    private final String name;
    private final NetworkImpl network;
    private final Channel channel;
    private final GatewayImpl gateway;
    private final CommitHandlerFactory commitHandlerFactory;
    private TimePeriod commitTimeout;
    private final QueryHandler queryHandler;
    private Map<String, byte[]> transientData = null;
    private Collection<Peer> endorsingPeers = null;
    private TransactionResult transactionResult;

    CustomTransaction(ContractImpl contract, String name, TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
        this.contract = contract;
        this.name = name;
        this.network = contract.getNetwork();
        this.channel = this.network.getChannel();
        this.gateway = this.network.getGateway();
        this.commitHandlerFactory = this.gateway.getCommitHandlerFactory();
        this.commitTimeout = this.gateway.getCommitTimeout();
        this.queryHandler = this.network.getQueryHandler();
    }

    public String getName() {
        return this.name;
    }

    public Transaction setTransient(Map<String, byte[]> transientData) {
        this.transientData = transientData;
        return this;
    }

    public Transaction setCommitTimeout(long timeout, TimeUnit timeUnit) {
        this.commitTimeout = new TimePeriod(timeout, timeUnit);
        return this;
    }

    public Transaction setEndorsingPeers(Collection<Peer> peers) {
        this.endorsingPeers = peers;
        return this;
    }

    public byte[] submit(String... args) throws ContractException, TimeoutException, InterruptedException {
        try {
            TransactionProposalRequest request = this.newProposalRequest(args);
            Collection<ProposalResponse> proposalResponses = this.sendTransactionProposal(request);
            Collection<ProposalResponse> validResponses = this.validatePeerResponses(proposalResponses);
            ProposalResponse proposalResponse = (ProposalResponse)validResponses.iterator().next();
            byte[] result = proposalResponse.getChaincodeActionResponsePayload();
            String transactionId = proposalResponse.getTransactionID();
            TransactionOptions transactionOptions = TransactionOptions.createTransactionOptions().nOfEvents(NOfEvents.createNoEvents());
            CustomCommitHandler commitHandler = (CustomCommitHandler) this.commitHandlerFactory.create(transactionId, this.network);
            commitHandler.setResult(transactionResult);
            commitHandler.startListening();

            try {
                this.channel.sendTransaction(validResponses, transactionOptions).get(60L, TimeUnit.SECONDS);
            } catch (TimeoutException var11) {
                commitHandler.cancelListening();
                throw var11;
            } catch (Exception var12) {
                commitHandler.cancelListening();
                throw new ContractException("Failed to send transaction to the orderer", var12);
            }

            commitHandler.waitForEvents(this.commitTimeout.getTime(), this.commitTimeout.getTimeUnit());
            return result;
        } catch (ProposalException | ServiceDiscoveryException | InvalidArgumentException var13) {
            throw new GatewayRuntimeException(var13);
        }
    }

    private TransactionProposalRequest newProposalRequest(String[] args) {
        TransactionProposalRequest request = this.network.getGateway().getClient().newTransactionProposalRequest();
        this.configureRequest(request, args);
        if (this.transientData != null) {
            try {
                request.setTransientMap(this.transientData);
            } catch (InvalidArgumentException var4) {
                throw new IllegalStateException(var4);
            }
        }

        return request;
    }

    private void configureRequest(TransactionRequest request, String[] args) {
        request.setChaincodeID(this.getChaincodeId());
        request.setFcn(this.name);
        request.setArgs(args);
    }

    private ChaincodeID getChaincodeId() {
        return ChaincodeID.newBuilder().setName(this.contract.getChaincodeId()).build();
    }

    private Collection<ProposalResponse> sendTransactionProposal(TransactionProposalRequest request) throws InvalidArgumentException, ServiceDiscoveryException, ProposalException {
        if (this.endorsingPeers != null) {
            return this.channel.sendTransactionProposal(request, this.endorsingPeers);
        } else if (this.network.getGateway().isDiscoveryEnabled()) {
            DiscoveryOptions discoveryOptions = DiscoveryOptions.createDiscoveryOptions().setEndorsementSelector(EndorsementSelector.ENDORSEMENT_SELECTION_RANDOM).setForceDiscovery(true);
            return this.channel.sendTransactionProposalToEndorsers(request, discoveryOptions);
        } else {
            return this.channel.sendTransactionProposal(request);
        }
    }

    private Collection<ProposalResponse> validatePeerResponses(Collection<ProposalResponse> proposalResponses) throws ContractException {
        Collection<ProposalResponse> validResponses = new ArrayList();
        Collection<String> invalidResponseMsgs = new ArrayList();
        proposalResponses.forEach((response) -> {
            String peerUrl = response.getPeer() != null ? response.getPeer().getUrl() : "<unknown>";
            if (response.getStatus().equals(Status.SUCCESS)) {
                logger.debug(String.format("validatePeerResponses: valid response from peer %s", peerUrl));
                validResponses.add(response);
            } else {
                logger.warn(String.format("validatePeerResponses: invalid response from peer %s, message %s", peerUrl, response.getMessage()));
                invalidResponseMsgs.add(response.getMessage());
            }

        });
        if (validResponses.size() < 1) {
            String msg = String.format("No valid proposal responses received. %d peer error responses: %s", invalidResponseMsgs.size(), String.join("; ", invalidResponseMsgs));
            transactionResult.setTransactionError(new TransactionError("PFail", msg,Instant.now()));
            logger.error(msg);
            throw new ContractException(msg);
        } else {
            transactionResult.setTimeAtReceive(Instant.now());
            return validResponses;
        }
    }

    public byte[] evaluate(String... args) throws ContractException {
        QueryByChaincodeRequest request = this.newQueryRequest(args);
        Query query = new QueryImpl(this.network.getChannel(), request);
        ProposalResponse response = this.queryHandler.evaluate(query);
        transactionResult.setTimeAtReceive(Instant.now());
        try {
            transactionResult.setTimeAtConfirm(Instant.now());
            return response.getChaincodeActionResponsePayload();
        } catch (InvalidArgumentException e) {
            transactionResult.setTransactionError(new TransactionError("QFail", e.getMessage(), Instant.now()));
            throw new ContractException(response.getMessage(), e);
        }
    }

    private QueryByChaincodeRequest newQueryRequest(String[] args) {
        QueryByChaincodeRequest request = this.gateway.getClient().newQueryProposalRequest();
        this.configureRequest(request, args);
        if (this.transientData != null) {
            try {
                request.setTransientMap(this.transientData);
            } catch (InvalidArgumentException var4) {
                throw new IllegalStateException(var4);
            }
        }

        return request;
    }
}
