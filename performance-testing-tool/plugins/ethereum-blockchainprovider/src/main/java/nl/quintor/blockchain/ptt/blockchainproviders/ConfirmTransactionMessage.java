package nl.quintor.blockchain.ptt.blockchainproviders;

import nl.quintor.blockchain.ptt.api.TransactionResult;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.time.Instant;

public class ConfirmTransactionMessage {
    private Instant lastChecked;
    private EthSendTransaction transaction;
    private TransactionResult result;

    public ConfirmTransactionMessage(EthSendTransaction transaction, TransactionResult result) {
        this.transaction = transaction;
        this.result = result;
    }

    public Instant getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }

    public EthSendTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(EthSendTransaction transaction) {
        this.transaction = transaction;
    }

    public TransactionResult getResult() {
        return result;
    }

    public void setResult(TransactionResult result) {
        this.result = result;
    }
}
