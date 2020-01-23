package nl.quintor.blockchain.ptt.blockchainproviders.messages;

import nl.quintor.blockchain.ptt.api.TransactionResult;

public class FinishedTransactionMessage {

    private TransactionResult result;

    public FinishedTransactionMessage(TransactionResult result) {
        this.result = result;
    }

    public TransactionResult getResult() {
        return result;
    }
}