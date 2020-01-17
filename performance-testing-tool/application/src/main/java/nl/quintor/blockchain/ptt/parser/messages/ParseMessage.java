package nl.quintor.blockchain.ptt.parser.messages;

import nl.quintor.blockchain.ptt.api.TransactionResult;

import java.time.Instant;
import java.util.List;

public class ParseMessage {
    private Instant beginning;

    public ParseMessage(Instant beginning, List<TransactionResult> getRawResults) {
        this.beginning = beginning;
        this.getRawResults = getRawResults;
    }

    public Instant getBeginning() {
        return beginning;
    }

    public void setBeginning(Instant beginning) {
        this.beginning = beginning;
    }

    private List<TransactionResult> getRawResults;

    public List<TransactionResult> getGetRawResults() {
        return getRawResults;
    }

    public void setGetRawResults(List<TransactionResult> getRawResults) {
        this.getRawResults = getRawResults;
    }
}
