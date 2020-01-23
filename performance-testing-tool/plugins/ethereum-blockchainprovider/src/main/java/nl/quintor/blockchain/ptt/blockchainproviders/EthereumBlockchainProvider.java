package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import io.reactivex.disposables.Disposable;
import nl.quintor.blockchain.ptt.api.TransactionError;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainProviderTransactionException;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumBlockchainConfig;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumFunction;
import nl.quintor.blockchain.ptt.blockchainproviders.config.FUNCTION_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class EthereumBlockchainProvider extends AbstractActorWithTimers {

    Logger logger = LoggerFactory.getLogger(EthereumBlockchainProvider.class);

    private EthereumBlockchainConfig blockchainConfig;
    private AsyncRawTransactionManager transactionManager;
    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider provider;
    private ActorRef testrunner;
    private Integer count = 0;
    private ActorRef confirmer;


    public EthereumBlockchainProvider(EthereumBlockchainConfig blockchainConfig) {
        this.blockchainConfig = blockchainConfig;
        this.provider = new DefaultGasProvider();
        web3j = Web3j.build(new HttpService(blockchainConfig.getNodeUrl()));
        credentials = Credentials.create(blockchainConfig.getWallet());
        transactionManager = new AsyncRawTransactionManager(web3j, credentials, (byte) 5777);
        this.confirmer = getContext().actorOf(Props.create(EthereumBlockchainConfirmer.class, blockchainConfig, getSelf()));
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SetupNetworkMessage.class, this::setupNetwork)
                .match(SendTransactionMessage.class, this::sendTransaction)
//                .match(ConfirmTransactionMessage.class, this::confirmTransaction)
                .match(TransactionResult.class, this::transactionConfirmed)
                .build();
    }

    private void transactionConfirmed(TransactionResult transactionResult) {
        testrunner.tell(transactionResult, getSelf());
    }


    public void setupNetwork(SetupNetworkMessage message) {
        if (message.getSetupConfigString() == null) {
            setupContractOnNetwork();
        }else{
            blockchainConfig.getContract().setAddress(message.getSetupConfigString());
        }
    }

    private void setupContractOnNetwork() {
        logger.debug("Setting up ethereum network");
        List<Type> constructorParameters = parseInputParameters(blockchainConfig.getContract().getConstructor().getInputParameters());
        String encodedContructor = FunctionEncoder.encodeConstructor(constructorParameters);
        try {
            EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(provider.getGasPrice(blockchainConfig.getContract().getConstructor().getName()),
                    provider.getGasLimit(blockchainConfig.getContract().getConstructor().getName()),
                    null, blockchainConfig.getContract().getBinary() + encodedContructor, blockchainConfig.getContract().getValue(), true);

            try {
                String contractAddress = null;
                String transactionHash = ethSendTransaction.getTransactionHash();
                Instant lastChecked = Instant.now();
                while (contractAddress == null) {
                    if (Instant.now().plusSeconds(blockchainConfig.getConfirmCheckInterval()).isAfter(lastChecked)) {
                        EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
                        if (transactionReceipt.getTransactionReceipt().isPresent()) {
                            contractAddress = transactionReceipt.getResult().getContractAddress();
                            blockchainConfig.getContract().setAddress(contractAddress);
                        }
                        lastChecked = Instant.now();
                    }
                }
                logger.info("Ethereum network is setup correctly with contract adres {}", contractAddress);
                getSender().tell(new SetupNetworkMessage(contractAddress), getSelf());
            } catch (IOException e) {
                logger.error("Failed getting contract address");
                getSender().tell("Failed Setting up Network", getSelf());
            }
        } catch (IOException e) {
            logger.error("Failed sending contract construction transaction");
            getSender().tell("Failed Setting up Network", getSelf());
        }

    }

    public void sendTransaction(SendTransactionMessage message) {
        if (testrunner == null) {
            testrunner = getSender();
        }
        if(blockchainConfig.getContract().getAddress() == null){
            logger.error("No contract address is given");
            getSender().tell(new TransactionResult(), getSelf());
            throw new BlockchainProviderTransactionException("No contract address is given");
        }else {
            String functionId = message.getFunctionId();
            EthereumFunction function = blockchainConfig.getContract().getFunction(functionId);
            TransactionResult transactionResult = new TransactionResult();
            if (function.getType().equals(FUNCTION_TYPE.TRANSACTION)) {
                logger.info("Sending transaction with function {}", functionId);
                sendTransaction(function, transactionResult);
            } else {
                logger.info("Querying network with function {}", functionId);
                queryBlockchain(function, transactionResult);
            }
        }
    }

    private void queryBlockchain(EthereumFunction ethFunction, TransactionResult result) {
        List<Type> inputParameters = parseInputParameters(ethFunction.getInputParameters());
        List<TypeReference<?>> outputParameters = parseOutputParameters(ethFunction.getOutputParameters());
        Function function = new Function(ethFunction.getName(), inputParameters, outputParameters);
        String encodedFunction = FunctionEncoder.encode(function);
        try {
            result.setTimeAtSend(Instant.now());
            String response = null;
            response = transactionManager.sendAsyncCall(blockchainConfig.getContract().getAddress(), encodedFunction).get().getValue();
            result.setTimeAtReceive(Instant.now());
            List<Type> responseVariables = FunctionReturnDecoder.decode(response, function.getOutputParameters());
            if (responseVariables.isEmpty()) {
                logger.error("Empty response was given");
                result.setTransactionError(new TransactionError("Eth 5", "Query gave empty response", Instant.now()));
            } else {
                result.setTimeAtConfirm(Instant.now());
                StringBuilder builder = new StringBuilder();
                for (Type var : responseVariables) {
                    builder.append(var.getValue().toString());
                    builder.append(" ");
                }
                logger.info("Query successful with response: {}", builder.toString());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Query failed", e);
            result.setTransactionError(new TransactionError("Eth 4", "Query failed to receive", Instant.now()));
        }
        getSender().tell(result, getSelf());
    }


    private void sendTransaction(EthereumFunction ethFunction, TransactionResult result) {
        List<Type> inputParameters = parseInputParameters(ethFunction.getInputParameters());
        List<TypeReference<?>> outputParameters = Collections.emptyList();
        Function function = new Function(ethFunction.getName(), inputParameters, outputParameters);
        String encodedFunction = FunctionEncoder.encode(function);
        try {
            result.setTimeAtSend(Instant.now());
            count++;
            EthSendTransaction transaction = transactionManager.sendAsyncTransaction(provider.getGasPrice(function.getName()),
                    provider.getGasLimit(function.getName()),
                    blockchainConfig.getContract().getAddress(), encodedFunction, BigInteger.ZERO, count).get();
            receiveTransaction(result, transaction, count);
        } catch (IOException e) {
            logger.error("Sending failed", e);
            result.setTransactionError(new TransactionError("Eth 1", "Transaction failed to send", Instant.now()));
            try {
                transactionManager.resetNonce();
            } catch (IOException ex) {
                logger.error("Couldn't reset nonce might be out of sync", ex);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Receiving failed");
            result.setTransactionError(new TransactionError("Eth 2", "Transaction failed to receive", Instant.now()));
        }
    }

    private void receiveTransaction(TransactionResult result, EthSendTransaction transaction, Integer count) {
        result.setTimeAtReceive(Instant.now());
        if (transaction.hasError()) {
            testrunner.tell(result, getSelf());
            logger.info("Transaction {} failed with error {}", count, transaction.getError().getMessage());
            result.setTransactionError(new TransactionError("Eth 2", "Transaction failed to receive", Instant.now()));
            throw new BlockchainProviderTransactionException("Transaction failed with error :" + transaction.getError().getMessage());
        } else {
//        Schedule the confirm polling
//            getTimers().startPeriodicTimer(transaction.getTransactionHash(), new ConfirmTransactionMessage(transaction, result), Duration.ofSeconds(blockchainConfig.getConfirmCheckInterval()));
            logger.info("Transaction {} received by the network", count);
            confirmer.tell(new ConfirmTransactionMessage(transaction, result), getSelf());
        }
    }

    private List<TypeReference<?>> parseOutputParameters(List<Object> outputParameters) {
        List<TypeReference<?>> parsedParameters = new ArrayList<>();
        for (Object parameter : outputParameters) {
            switch (parameter.toString()) {
                case "Uint8":
                    parsedParameters.add(new TypeReference<Uint8>() {
                    });
                    break;
                case "Utf8String":
                    parsedParameters.add(new TypeReference<Utf8String>() {
                    });
                    break;
                default:
                    System.out.println("Unsupported type " + parameter.getClass().toString());
            }
        }
        return parsedParameters;
    }

    private List<Type> parseInputParameters(List<Object> inputParameters) {
        List<Type> parsedParameters = new ArrayList<>();
        for (Object parameter : inputParameters) {
            switch (parameter.getClass().toString()) {
                case "class java.lang.String":
                    try {
                        parsedParameters.add(new Address((String) parameter));
                    } catch (Exception e) {
                        parsedParameters.add(new Utf8String((String) parameter));
                    }
                    break;
                case "class java.lang.Integer":
                    parsedParameters.add(new Uint8((Integer) parameter));
                    break;
                default:
                    System.out.println("Unsupported type " + parameter.getClass().toString());
            }
        }
        return parsedParameters;
    }
}
