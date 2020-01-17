package nl.quintor.blockchain.ptt.parser;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

public class StatusOverviewTimelineBuilder {
    private Map<Instant, StatusOverviewTimelineSnapshot> timeline = new TreeMap<>();
    private Integer amountSend;
    private Integer amountReceived;
    private Integer amountConfirmed;
    private Integer amountFailed;

    public StatusOverviewTimelineBuilder() {
        this.amountSend = 0;
        this.amountReceived = 0;
        this.amountConfirmed = 0;
        this.amountFailed=0;
    }

    public StatusOverviewTimelineBuilder(Integer amountToSend, Integer amountToReceive, Integer amountConfirmed, Integer amountFailed) {
        this.amountSend = amountToSend;
        this.amountReceived = amountToReceive;
        this.amountConfirmed = amountConfirmed;
        this.amountFailed = amountFailed;
    }

    public void createSnapshot(Instant time, EventType type){
        switch(type){
            case SEND:
                amountSend++;
                break;
            case RECEIVED:
                amountSend--;
                amountReceived++;
                break;
            case CONFIRMED:
                amountReceived--;
                amountConfirmed++;
                break;
            case FAILED_AT_RECEIVE:
                amountSend--;
                amountFailed++;
                break;
            case FAILED_AT_CONFIRM:
                amountReceived--;
                amountFailed++;
                break;
            default:
                //Do nothing for INVALID types
                break;
        }
        timeline.put(time, new StatusOverviewTimelineSnapshot(amountSend, amountReceived, amountConfirmed, amountFailed));
    }

    public Map<Instant, StatusOverviewTimelineSnapshot> build() {
        return timeline;
    }
}
