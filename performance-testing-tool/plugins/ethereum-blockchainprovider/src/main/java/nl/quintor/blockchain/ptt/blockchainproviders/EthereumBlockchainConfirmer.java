package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumBlockchainConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class EthereumBlockchainConfirmer extends AbstractActorWithTimers {
    Logger logger = LoggerFactory.getLogger(EthereumBlockchainConfirmer.class);
    private EthereumBlockchainConfig blockchainConfig;
    private AsyncRawTransactionManager transactionManager;
    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider provider;
    private ActorRef blockchainProvider;
    private Map<String, TransactionResult> toConfirmedResults;
    private BigInteger lastCheckedBlockNumber;


    public EthereumBlockchainConfirmer(EthereumBlockchainConfig blockchainConfig, ActorRef blockchainProvider) {
        this.blockchainConfig = blockchainConfig;
        this.blockchainProvider = blockchainProvider;
        this.toConfirmedResults = new HashMap<>();
        this.provider = new DefaultGasProvider();
        web3j = Web3j.build(new HttpService(blockchainConfig.getNodeUrl()));
        credentials = Credentials.create(blockchainConfig.getWallet());
        transactionManager = new AsyncRawTransactionManager(web3j, credentials, (byte) 5777);
        getSelf().tell(new ConfirmBlockMessage(), getSelf());
        getTimers().startPeriodicTimer("latest", new ConfirmBlockMessage(), Duration.ofSeconds(blockchainConfig.getConfirmCheckInterval()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ConfirmTransactionMessage.class, this::addToConfirmList)
                .match(ConfirmBlockMessage.class, this::pollBlock)
                .build();
    }

    private void pollBlock(ConfirmBlockMessage message) throws IOException {
        EthBlock.Block block;
        if (lastCheckedBlockNumber == null) {
            block = getBlock(DefaultBlockParameterName.LATEST);
        } else {
            if(message.getBlockHash() == null){
                block = getBlock(DefaultBlockParameterName.LATEST);
            }else{
                block = getBlock(message.getBlockHash());
            }
        }
        if(lastCheckedBlockNumber != null && lastCheckedBlockNumber.compareTo(block.getNumber()) < 0 && lastCheckedBlockNumber.compareTo(block.getNumber().subtract(BigInteger.ONE)) < 0){
            pollBlock(new ConfirmBlockMessage(block.getParentHash()));
        }
        logger.info("Checking Block {}", block.getHash());
        checkBlock(block);
        lastCheckedBlockNumber = block.getNumber();
    }

    private EthBlock.Block getBlock(String blockHash) throws IOException {
        return web3j.ethGetBlockByHash(blockHash, true).send().getBlock();
    }

    private void checkBlock(EthBlock.Block block) {
        Instant timeForBlock = Instant.now();
        for (EthBlock.TransactionResult transactionResult : block.getTransactions()) {
            Transaction transaction = (EthBlock.TransactionObject) transactionResult.get();
            if (toConfirmedResults.containsKey(transaction.getHash())) {
                TransactionResult result = toConfirmedResults.get(transaction.getHash());
                result.setTimeAtConfirm(timeForBlock);
                logger.info("Transaction {} confirmed in block {}", transaction.getHash(), block.getHash());
                blockchainProvider.tell(result, getSelf());
                toConfirmedResults.remove(transaction.getHash());
            }
        }
    }

    private EthBlock.Block getBlock(DefaultBlockParameterName blockParameterName) throws IOException {
        return web3j.ethGetBlockByNumber(blockParameterName, true).send().getBlock();
    }

    private void addToConfirmList(ConfirmTransactionMessage message) {
        toConfirmedResults.put(message.getTransaction().getTransactionHash(), message.getResult());
    }

}
