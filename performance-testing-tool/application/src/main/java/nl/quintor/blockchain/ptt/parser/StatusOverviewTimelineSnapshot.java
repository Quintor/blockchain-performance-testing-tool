package nl.quintor.blockchain.ptt.parser;

public class StatusOverviewTimelineSnapshot {
    private Integer amountSend;
    private Integer amountReceived;
    private Integer amountConfirmed;
    private Integer amountFailed;

    public StatusOverviewTimelineSnapshot() {
    }

    public StatusOverviewTimelineSnapshot(Integer amountSend, Integer amountReceived, Integer amountConfirmed, Integer amountFailed) {
        this.amountSend = amountSend;
        this.amountReceived = amountReceived;
        this.amountConfirmed = amountConfirmed;
        this.amountFailed = amountFailed;
    }

    public Integer getAmountSend() {
        return amountSend;
    }

    public void setAmountSend(Integer amountSend) {
        this.amountSend = amountSend;
    }

    public Integer getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(Integer amountReceived) {
        this.amountReceived = amountReceived;
    }

    public Integer getAmountConfirmed() {
        return amountConfirmed;
    }

    public void setAmountConfirmed(Integer amountConfirmed) {
        this.amountConfirmed = amountConfirmed;
    }

    public Integer getAmountFailed() {
        return amountFailed;
    }

    public void setAmountFailed(Integer amountFailed) {
        this.amountFailed = amountFailed;
    }
}
