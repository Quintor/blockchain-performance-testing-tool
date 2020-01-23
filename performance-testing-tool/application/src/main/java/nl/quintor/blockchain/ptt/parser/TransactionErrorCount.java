package nl.quintor.blockchain.ptt.parser;

import nl.quintor.blockchain.ptt.api.TransactionError;

public class TransactionErrorCount {
    private Integer count;
    private TransactionError error;

    public TransactionErrorCount() {
    }

    public TransactionErrorCount(TransactionError error) {
        this.count = 0;
        this.error = error;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setError(TransactionError error) {
        this.error = error;
    }

    public TransactionError getError() {
        return error;
    }

    public void addCount(){
        count++;
    }
}
