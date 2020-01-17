package nl.quintor.blockchain.ptt.parser;

import akka.actor.AbstractActor;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.parser.messages.ParseMessage;
import nl.quintor.blockchain.ptt.parser.messages.ParseResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class TransactionResultParser extends AbstractActor {


    private Logger logger = LoggerFactory.getLogger(TransactionResultParser.class);
    private ParsedMetrics parsedMetrics;


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ParseMessage.class, this::parse)
                .build();
    }

    private void parse(ParseMessage message) {
        if (message.getGetRawResults() == null || message.getGetRawResults().isEmpty()) {
            logger.warn("No results to parse");
            getSender().tell(new ParseResultMessage(), getSelf());
        } else {
            parsedMetrics = new ParsedMetrics();

            List<ProviderEvent> eventList = generateEventList(message.getGetRawResults());

            parseEvents(eventList, message);

            ParseResultMessage parseResultMessage = new ParseResultMessage(parsedMetrics);
            getSender().tell(parseResultMessage, getSelf());
        }
    }

    private void parseEvents(List<ProviderEvent> eventList, ParseMessage message) {
        StatusOverviewTimelineBuilder statusOverviewTimelineBuilder = new StatusOverviewTimelineBuilder();
        Map<String, TransactionErrorCount> errorMap = new HashMap<>();
        List<Long> responseTimesToReceive = new ArrayList<>();
        List<Long> responseTimesToConfirm = new ArrayList<>();
        Map<Instant, Double> transactionsPerSecondMap = new TreeMap<>();
        Integer totalAmountOfTransactionsSend = 0;

        for (ProviderEvent event : eventList) {
            logger.debug("Event type {}", event.getType());
            statusOverviewTimelineBuilder.createSnapshot(event.getTime(), event.getType());
            switch (event.getType()) {
                case SEND:
                    totalAmountOfTransactionsSend++;
                    break;
                case RECEIVED:
                    responseTimesToReceive.add(Duration.between(event.getResult().getTimeAtSend(), event.getResult().getTimeAtReceive()).toMillis());
                    break;
                case CONFIRMED:
                    responseTimesToConfirm.add(Duration.between(event.getResult().getTimeAtSend(), event.getResult().getTimeAtConfirm()).toMillis());
                    transactionsPerSecondMap.put(event.getTime(),  ((double) responseTimesToConfirm.size() / Duration.between(message.getBeginning(), event.getTime()).toSeconds()));
                    break;
                case FAILED_AT_CONFIRM:
                case FAILED_AT_RECEIVE:
                    errorMap.compute(event.getResult().getTransactionError().getCode(), (code, error) -> putOrAddError(error, event));
                    break;
                default:
//                    Do nothing for invalid types
                    logger.warn("invalid event type read");
                    break;
            }
        }
        Collections.sort(responseTimesToReceive);
        Collections.sort(responseTimesToConfirm);

        parsedMetrics.setStatusTimeline(statusOverviewTimelineBuilder.build());
        parsedMetrics.setResponseTimesToReceive(responseTimesToReceive);
        parsedMetrics.setResponseTimesToConfirm(responseTimesToConfirm);
        parsedMetrics.setTransactionsPerSecond(transactionsPerSecondMap);
        parsedMetrics.setErrorMap(errorMap);

        if(totalAmountOfTransactionsSend <= 0){
            parsedMetrics.setReceiveSuccesRate(0f);
            parsedMetrics.setConfirmSuccesRate(0f);
        }else{
            parsedMetrics.setReceiveSuccesRate((float) responseTimesToReceive.size() / totalAmountOfTransactionsSend);
            parsedMetrics.setConfirmSuccesRate((float) responseTimesToConfirm.size() / totalAmountOfTransactionsSend);
        }

        parsedMetrics.setBeginTime(message.getBeginning());
        Instant endTime = eventList.get(eventList.size() - 1).getTime();
        parsedMetrics.setEndTime(endTime);
    }

    private TransactionErrorCount putOrAddError(TransactionErrorCount error, ProviderEvent event) {
        if (error == null) {
            return new TransactionErrorCount(event.getResult().getTransactionError());
        } else {
            error.addCount();
            return error;
        }
    }

    /**
     *  This method loops through all results to generate an eventList
     *  This list contains everything that happens to a transaction (send, received etc.) and the corresponding transactionresult
     *  This list is used to parse every metric but mainly to build the StatusOverviewTimeLine
     *  by going through the events and adding or substracting the status amounts
     * @param rawTransactionResults
     * @return a list of all events (ProviderEvent) of all transactions in <b>chronological order</b>
     */
    private List<ProviderEvent> generateEventList(List<TransactionResult> rawTransactionResults) {
        List<ProviderEvent> eventList = new LinkedList<>();
        for (TransactionResult transactionResult : rawTransactionResults) {
//          Transactions without a valid end state are ignored - Invalid end state means not send or not confirmed or failed
            if (!transactionResult.isInvalidEndState()) {
                logger.warn("Transaction result from runner {} has a invalid end state", transactionResult.getRunner());
                eventList.add(new ProviderEvent(null, EventType.INVALID, transactionResult));
            } else {
                eventList.add(new ProviderEvent(transactionResult.getTimeAtSend(), EventType.SEND, transactionResult));
                if (transactionResult.getTimeAtReceive() != null) {
                    eventList.add(new ProviderEvent(transactionResult.getTimeAtReceive(), EventType.RECEIVED, transactionResult));
                    if (transactionResult.getTimeAtConfirm() != null) {
                        eventList.add(new ProviderEvent(transactionResult.getTimeAtConfirm(), EventType.CONFIRMED, transactionResult));
                    } else {
                        eventList.add(new ProviderEvent(transactionResult.getTransactionError().getTime(), EventType.FAILED_AT_CONFIRM, transactionResult));
                    }
                } else {
                    eventList.add(new ProviderEvent(transactionResult.getTransactionError().getTime(), EventType.FAILED_AT_RECEIVE, transactionResult));
                }
            }
        }

        Collections.sort(eventList);
        return eventList;
    }
}
