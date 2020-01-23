package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerResultMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerTerminateMessage;
import nl.quintor.blockchain.ptt.parser.messages.ParseMessage;
import nl.quintor.blockchain.ptt.parser.messages.ParseResultMessage;
import nl.quintor.blockchain.ptt.report.messages.PrintReportMessage;
import nl.quintor.blockchain.ptt.report.messages.PrintReportResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestRunnerManager extends AbstractActor {
    Logger logger = LoggerFactory.getLogger(TestRunnerManager.class);

    private List<ActorRef> testRunners;
    private List<TransactionResult> results;
    private Integer running;
    private ActorRef metricParser;
    private ActorRef reportGenerator;
    private Instant beginning;

    public TestRunnerManager(List<ActorRef> testRunners, ActorRef metricParser, ActorRef reportGenerator) {
        this.results = new ArrayList<>();
        this.running = testRunners.size();
        this.testRunners = testRunners;
        this.metricParser = metricParser;
        this.reportGenerator = reportGenerator;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestRunnerStartMessage.class, this::start)
                .match(TestRunnerResultMessage.class, this::addResultToList)
                .match(TestRunnerTerminateMessage.class, this::terminate)
                .match(ParseResultMessage.class, this::sendPrintMessage)
                .match(PrintReportResultMessage.class, this::shutdown)
                .build();
    }

    private void shutdown(PrintReportResultMessage message) {
        getContext().getSystem().terminate();
    }

    private void terminate(TestRunnerTerminateMessage message) {
        logger.info("Starting Termination Testrunners");
        for (ActorRef testRunner : testRunners) {
            testRunner.tell(message, getSelf());
        }
    }

    private void start(TestRunnerStartMessage message) {
        logger.info("Starting Testrunners");
        beginning = Instant.now();
        for (ActorRef testRunner : testRunners) {
            testRunner.tell(message, getSelf());
        }
    }

    private void addResultToList(TestRunnerResultMessage message) {
        running--;
        if (running >= 0) {
            results.addAll(message.getResultList());
            if (running == 0) {
                sendParseMessage();
            }
        }
    }

    private void sendParseMessage() {
        metricParser.tell(new ParseMessage(beginning, results), getSelf());
    }

    private void sendPrintMessage(ParseResultMessage message){
        if(message.getParsedMetrics() == null){
            logger.warn("Parser gave no metrics shutting down");
            getContext().getSystem().terminate();
        }else{
            PrintReportMessage printReportMessage = new PrintReportMessage(message.getParsedMetrics(), results);
            reportGenerator.tell(printReportMessage, getSelf());
        }
    }
}
