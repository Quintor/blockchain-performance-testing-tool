package nl.quintor.blockchain.ptt.parser.messages;

import nl.quintor.blockchain.ptt.parser.ParsedMetrics;

public class ParseResultMessage {
    private ParsedMetrics parsedMetrics;

    public ParseResultMessage() {
    }

    public ParseResultMessage(ParsedMetrics parsedMetrics) {
        this.parsedMetrics = parsedMetrics;
    }

    public void setParsedMetrics(ParsedMetrics parsedMetrics) {
        this.parsedMetrics = parsedMetrics;
    }

    public ParsedMetrics getParsedMetrics() {
        return parsedMetrics;
    }
}
