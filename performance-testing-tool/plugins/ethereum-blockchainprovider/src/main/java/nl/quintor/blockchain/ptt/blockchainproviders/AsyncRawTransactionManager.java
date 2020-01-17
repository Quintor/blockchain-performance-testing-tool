package nl.quintor.blockchain.ptt.blockchainproviders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.FastRawTransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public class AsyncRawTransactionManager extends FastRawTransactionManager {
    Logger logger = LoggerFactory.getLogger(AsyncRawTransactionManager.class);

    private final Web3j web3j;
    private final Credentials credentials;

    public AsyncRawTransactionManager(Web3j web3j, Credentials credentials, byte chainId) {
        super(web3j, credentials, chainId);
        this.web3j = web3j;
        this.credentials = credentials;
    }

    
    public synchronized CompletableFuture<EthSendTransaction> sendAsyncTransaction(BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value, Integer count) throws IOException {
        BigInteger nonce = getNonce();
        logger.info("Sending transaction {} with nonce {}", count, nonce);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        String hexValue = this.sign(rawTransaction);
        return this.web3j.ethSendRawTransaction(hexValue).sendAsync();
    }

    public CompletableFuture<EthCall> sendAsyncCall(String contractAddress, String encodedFunction){
        return web3j.ethCall(Transaction.createEthCallTransaction(credentials.getAddress(), contractAddress, encodedFunction), DefaultBlockParameterName.LATEST).sendAsync();
    }
}
