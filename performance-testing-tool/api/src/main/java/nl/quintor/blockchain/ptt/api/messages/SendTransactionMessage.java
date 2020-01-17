package nl.quintor.blockchain.ptt.api.messages;


/**
 * A Message class for communicating between actors
 * <b>Sent by</b> a test runner
 * <b>Received by</b> a Blockchain Provider
 * <b>Used for</b> signaling the blockchain provider to send a transaction to the network
 * <b>Response</b> After a transaction is confirmed or failed return a TransactionResult object with variables
 */
public class SendTransactionMessage {
    /**
     * Used to identify inside the blockchain provider which variables it needs to use in the transaction
     * Such as method name, input parameters etc.
     */
    private String functionId;

    public SendTransactionMessage(String functionId) {
        this.functionId = functionId;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }
}
