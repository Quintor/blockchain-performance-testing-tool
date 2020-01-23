package nl.quintor.blockchain.ptt.testrunners.mocks;

import nl.quintor.blockchain.ptt.api.TransactionResult;

public class ConfirmTransactionMessage {
    private TransactionResult transactionResult;

    public ConfirmTransactionMessage(TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
    }

    public TransactionResult getTransactionResult() {
        return transactionResult;
    }
}
