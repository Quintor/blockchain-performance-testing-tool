package nl.quintor.blockchain.ptt.testrunners.mocks;

import nl.quintor.blockchain.ptt.api.TransactionResult;

public class ReceiveTransactionMessage {
    private TransactionResult transactionResult;

    public ReceiveTransactionMessage(TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
    }

    public TransactionResult getTransactionResult() {
        return transactionResult;
    }
}
