package nl.quintor.blockchain.ptt.api;

import java.time.Instant;

public class TransactionResult{
    private String runner;
    private TransactionError transactionError;
    private Instant timeAtSend;
    private Instant timeAtReceive;
    private Instant timeAtConfirm;

    public TransactionError getTransactionError() {
        return transactionError;
    }

    public void setTransactionError(TransactionError transactionError) {
        this.transactionError = transactionError;
    }

    public String getRunner() {
        return runner;
    }

    public void setRunner(String runner) {
        this.runner = runner;
    }

    public Instant getTimeAtSend() {
        return timeAtSend;
    }

    public void setTimeAtSend(Instant timeAtSend) {
        this.timeAtSend = timeAtSend;
    }

    public Instant getTimeAtReceive() {
        return timeAtReceive;
    }

    public void setTimeAtReceive(Instant timeAtReceive) {
        this.timeAtReceive = timeAtReceive;
    }

    public Instant getTimeAtConfirm() {
        return timeAtConfirm;
    }

    public void setTimeAtConfirm(Instant timeAtConfirm) {
        this.timeAtConfirm = timeAtConfirm;
    }

    /**
     * Checks if the TransactionResult is in a valid end state (either confirmed or failed not both)
     * Invalid end state TransactionResults are ignored by the parser
     * @return
     */
    public boolean isInvalidEndState(){
        return timeAtSend != null && (
                ((timeAtReceive == null && timeAtConfirm == null) && transactionError != null) ||
                        ((timeAtReceive != null && timeAtConfirm == null) && transactionError != null) ||
                        (timeAtReceive != null && timeAtConfirm != null));
    }

}
