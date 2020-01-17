package nl.quintor.blockchain.ptt.parser;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ParsedMetrics {
    private Instant beginTime;
    private Instant endTime;
    private Float receiveSuccesRate;
    private Float confirmSuccesRate;

    private List<Long> responseTimesToReceive;
    private List<Long> responseTimesToConfirm;

    private Map<Instant, StatusOverviewTimelineSnapshot> statusTimeline;
    private Map<Instant, Double> transactionsPerSecond;
    private Map<String, TransactionErrorCount> errorMap;

    public Instant getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Instant beginTime) {
        this.beginTime = beginTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Float getReceiveSuccesRate() {
        return receiveSuccesRate;
    }

    public void setReceiveSuccesRate(Float receiveSuccesRate) {
        this.receiveSuccesRate = receiveSuccesRate;
    }

    public Float getConfirmSuccesRate() {
        return confirmSuccesRate;
    }

    public void setConfirmSuccesRate(Float confirmSuccesRate) {
        this.confirmSuccesRate = confirmSuccesRate;
    }

    public List<Long> getResponseTimesToReceive() {
        return responseTimesToReceive;
    }

    public void setResponseTimesToReceive(List<Long> responseTimesToReceive) {
        this.responseTimesToReceive = responseTimesToReceive;
    }

    public List<Long> getResponseTimesToConfirm() {
        return responseTimesToConfirm;
    }

    public void setResponseTimesToConfirm(List<Long> responseTimesToConfirm) {
        this.responseTimesToConfirm = responseTimesToConfirm;
    }

    public Map<Instant, StatusOverviewTimelineSnapshot> getStatusTimeline() {
        return statusTimeline;
    }

    public void setStatusTimeline(Map<Instant, StatusOverviewTimelineSnapshot> statusTimeline) {
        this.statusTimeline = statusTimeline;
    }

    public Map<Instant, Double> getTransactionsPerSecond() {
        return transactionsPerSecond;
    }

    public void setTransactionsPerSecond(Map<Instant, Double> transactionsPerSecond) {
        this.transactionsPerSecond = transactionsPerSecond;
    }

    public Map<String, TransactionErrorCount> getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(Map<String, TransactionErrorCount> errorMap) {
        this.errorMap = errorMap;
    }
}
