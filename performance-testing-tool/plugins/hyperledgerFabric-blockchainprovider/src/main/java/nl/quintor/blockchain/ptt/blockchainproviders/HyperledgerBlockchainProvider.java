package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionError;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import nl.quintor.blockchain.ptt.blockchainproviders.messages.FinishedTransactionMessage;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class HyperledgerBlockchainProvider extends AbstractActorWithTimers {

    private Logger logger = LoggerFactory.getLogger(HyperledgerBlockchainProvider.class);

    private HyperledgerBlockchainProviderConfig blockchainConfig;

    Map<String, TransactionResult> transactionResultMap;

    private GatewayImpl gateway;
    private NetworkImpl network;
    private ContractImpl contract;

    private ActorRef testrunner;


    public HyperledgerBlockchainProvider(HyperledgerBlockchainProviderConfig blockchainConfig) throws BlockchainConfigException {
        this.blockchainConfig = blockchainConfig;
        this.transactionResultMap = new HashMap<>();
        connectToContract();
    }

    private void connectToContract() throws BlockchainConfigException {
        // Load an existing wallet holding identities used to access the network.
        Wallet wallet = getWallet();
        // Path to a common connection profile describing the network.
        Path networkConfigFile = Paths.get(blockchainConfig.getNetworkConfigFile());

        // Configure the gateway connection used to access the network.
        try {
            GatewayImpl.Builder builder = (GatewayImpl.Builder) Gateway.createBuilder()
                    .identity(wallet, blockchainConfig.getUsername())
                    .networkConfig(networkConfigFile);
            builder.commitHandler(new CustomCommitHandlerFactory(getSelf()));
            gateway = builder.connect();
            network = (NetworkImpl) gateway.getNetwork(blockchainConfig.getNetworkName());
            contract = (ContractImpl) network.getContract(blockchainConfig.getContractName());
        } catch (IOException e) {
            throw new BlockchainConfigException("Failed to connect to network", e);
        }
    }

    private Wallet getWallet() {
        Wallet wallet = Wallet.createInMemoryWallet();
        try {
            Reader certReader = Files.newBufferedReader(Path.of(blockchainConfig.getUserCertificate()));
            Reader keyReader = Files.newBufferedReader(Path.of(blockchainConfig.getUserPrivateKey()));
            wallet.put(blockchainConfig.getUsername(), Wallet.Identity.createIdentity(blockchainConfig.getOrgMsp(), certReader, keyReader));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wallet;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SetupNetworkMessage.class, this::setupNetwork)
                .match(SendTransactionMessage.class, this::sendTransaction)
                .match(FinishedTransactionMessage.class, this::confirmTransaction)
                .build();
    }

    private void confirmTransaction(FinishedTransactionMessage message) {
        testrunner.tell(message.getResult(), getSelf());
    }
    private void setupNetwork(SetupNetworkMessage message) {
//      Implement setting up the network
//      add setup values relevant for other instances of this provider to the message and return it
        message.setSetupConfigString("variabelen");
        getSender().tell(message, getSelf());
    }


    private void sendTransaction(SendTransactionMessage message) {
        if(testrunner == null){
            testrunner = getSender();
        }
        HyperledgerFunction function = blockchainConfig.getFunctionList().get(message.getFunctionId());
        TransactionResult transactionResult = new TransactionResult();
        Transaction transaction = createTransaction(function.getName(), transactionResult);

        Integer tryCount = 0;
        boolean isFinished = false;
        transactionResult.setTimeAtSend(Instant.now());
        while(!isFinished){
            tryCount++;
            try{
                if(function.getType().equals("QUERY")){
                    transaction.evaluate(function.getArguments());
                }else {
                    transaction.submit(function.getArguments());
                }
                isFinished = true;
            } catch (InterruptedException | TimeoutException | ContractException e) {
                logger.error("Transaction with name {} errored, trying again try count {}", function.getName(), tryCount, e);
                if(tryCount >= blockchainConfig.getMaxAttempts()){
                    transactionResult.setTransactionError(new TransactionError("CFAIL", e.getMessage(), Instant.now()));
                    isFinished = true;
                }
            }
        }
        testrunner.tell(transactionResult, getSelf());
    }
    private Transaction createTransaction(String name, TransactionResult transactionResult) {
        if (name != null && !name.isEmpty()) {
            return new CustomTransaction(contract, name, transactionResult);
        } else {
            throw new IllegalArgumentException("Transaction must be a non-empty string");
        }
    }

}
