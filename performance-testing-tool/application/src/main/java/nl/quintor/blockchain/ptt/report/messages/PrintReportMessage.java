package nl.quintor.blockchain.ptt.report.messages;

import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.parser.ParsedMetrics;

import java.util.List;

public class PrintReportMessage {
    private ParsedMetrics parsedResults;
    private List<TransactionResult> rawResults;

    public PrintReportMessage(ParsedMetrics parsedResults, List<TransactionResult> rawResults) {
        this.parsedResults = parsedResults;
        this.rawResults = rawResults;
    }

    public ParsedMetrics getParsedResults() {
        return parsedResults;
    }

    public void setParsedResults(ParsedMetrics parsedResults) {
        this.parsedResults = parsedResults;
    }

    public List<TransactionResult> getRawResults() {
        return rawResults;
    }

    public void setRawResults(List<TransactionResult> rawResults) {
        this.rawResults = rawResults;
    }
}
