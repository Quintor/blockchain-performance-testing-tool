package nl.quintor.blockchain.ptt.report.messages;

public class PrintReportResultMessage {
    private boolean succes;

    public PrintReportResultMessage(boolean succes) {
        this.succes = succes;
    }

    public boolean isSucces() {
        return succes;
    }
}
