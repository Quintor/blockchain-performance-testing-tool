package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerResultMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerTerminateMessage;
import nl.quintor.blockchain.ptt.report.messages.PrintReportMessage;
import nl.quintor.blockchain.ptt.parser.ParsedMetrics;
import nl.quintor.blockchain.ptt.parser.messages.ParseMessage;
import nl.quintor.blockchain.ptt.parser.messages.ParseResultMessage;
import nl.quintor.blockchain.ptt.report.messages.PrintReportResultMessage;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class estRunnerManagerTest {

    private static final int RUNNERAMOUNT = 10;

    private ActorSystem akkaSystem;

    private List<TestKit> testRunnerProbes;
    private TestKit metricParserProbe;
    private TestKit reportGeneratorProbe;
    private TestKit testKit;
    private ActorRef sut;

    @AfterEach
    public void teardown() {
        akkaSystem.terminate();
    }

    @BeforeEach
    private void setup() {
        akkaSystem = ActorSystem.create();
        testRunnerProbes = new ArrayList<>();
        List<ActorRef> testRunnerProbeRefs = new ArrayList<>();
        for (int i = 0; i < RUNNERAMOUNT; i++) {
            TestKit testRunnerProbe = new TestKit(akkaSystem);
            testRunnerProbes.add(testRunnerProbe);
            testRunnerProbeRefs.add(testRunnerProbe.getRef());
        }
        metricParserProbe = new TestKit(akkaSystem);
        reportGeneratorProbe = new TestKit(akkaSystem);
        sut = akkaSystem.actorOf(Props.create(TestRunnerManager.class, testRunnerProbeRefs, metricParserProbe.getRef(), reportGeneratorProbe.getRef()));
        testKit = new TestKit(akkaSystem);
    }

    @Test
    public void givenReportGeneratorIsDoneThenTestRunnerManagerShutdown() {
        testKit.within(Duration.ofSeconds(3), () -> {
            testKit.watch(sut);
            sut.tell(new TestRunnerStartMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                TestRunnerResultMessage message = new TestRunnerResultMessage();
                List<TransactionResult> resultList = new ArrayList<>();
                resultList.add(new TransactionResult());
                message.setResultList(resultList);
                sut.tell(message, testRunnerProbe.getRef());
            }

            metricParserProbe.receiveN(1);
            metricParserProbe.reply(new ParseResultMessage(new ParsedMetrics()));
            PrintReportMessage printReportMessage = reportGeneratorProbe.expectMsgClass(PrintReportMessage.class);
            reportGeneratorProbe.reply(new PrintReportResultMessage(true));
            testKit.expectTerminated(sut);
            return null;
        });
    }

    @Test
    public void givenTestRunnerTerminateMessageThenEveryTestRunnersGetTerminateMessage() {
        testKit.within(Duration.ofSeconds(3), () -> {
            sut.tell(new TestRunnerTerminateMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                testRunnerProbe.expectMsgClass(TestRunnerTerminateMessage.class);
            }
            return null;
        });
    }

    @Test
    public void givenTestRunnerStartMessageThenEveryTestRunnersGetsStartMessage() {
        testKit.within(Duration.ofSeconds(3), () -> {
            sut.tell(new TestRunnerStartMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                testRunnerProbe.expectMsgClass(TestRunnerStartMessage.class);
            }
            return null;
        });
    }

    @Test
    public void givenTestRunnerStartMessageThenMetricParserGetsMessageAfter() {
        testKit.within(Duration.ofSeconds(3), () -> {
            sut.tell(new TestRunnerStartMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                TestRunnerResultMessage message = new TestRunnerResultMessage();
                message.setResultList(new ArrayList<>());
                sut.tell(message, testRunnerProbe.getRef());
            }
            metricParserProbe.expectMsgClass(ParseMessage.class);
            return null;
        });
    }

    @Test
    public void givenTestRunnerMoreResultMessagesThenTestRunnersThenMetricParserGetsOnly1Message() {
        testKit.within(Duration.ofSeconds(3), () -> {
            sut.tell(new TestRunnerStartMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                TestRunnerResultMessage message = new TestRunnerResultMessage();
                message.setResultList(new ArrayList<>());
                sut.tell(message, testRunnerProbe.getRef());
            }
            sut.tell(new TestRunnerResultMessage(new ArrayList<>()), testRunnerProbes.get(0).getRef());
            metricParserProbe.receiveN(1);
            metricParserProbe.reply(new ParseResultMessage(new ParsedMetrics()));
            reportGeneratorProbe.expectMsgClass(PrintReportMessage.class);
            return null;
        });
    }
    @Test
    public void givenNoParsedMetricsThenShutdown() {
        testKit.within(Duration.ofSeconds(3), () -> {
            testKit.watch(sut);
            sut.tell(new TestRunnerStartMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                TestRunnerResultMessage message = new TestRunnerResultMessage();
                message.setResultList(new ArrayList<>());
                sut.tell(message, testRunnerProbe.getRef());
            }
            sut.tell(new TestRunnerResultMessage(new ArrayList<>()), testRunnerProbes.get(0).getRef());
            metricParserProbe.receiveN(1);
            metricParserProbe.reply(new ParseResultMessage());
            testKit.expectTerminated(sut);
            return null;
        });
    }

    @Test
    public void givenTestRunnerMoreResultMessagesThenTestRunnersThenExcessResultsDontGetAddedToList() {
        testKit.within(Duration.ofSeconds(3), () -> {
            sut.tell(new TestRunnerStartMessage(), testKit.getRef());
            for (TestKit testRunnerProbe : testRunnerProbes) {
                TestRunnerResultMessage message = new TestRunnerResultMessage();
                List<TransactionResult> resultList = new ArrayList<>();
                resultList.add(new TransactionResult());
                message.setResultList(resultList);
                sut.tell(message, testRunnerProbe.getRef());
            }

            List<TransactionResult> resultList = new ArrayList<TransactionResult>();
            resultList.add(new TransactionResult());
            sut.tell(new TestRunnerResultMessage(resultList), testRunnerProbes.get(0).getRef());
            metricParserProbe.receiveN(1);
            metricParserProbe.reply(new ParseResultMessage(new ParsedMetrics()));
            PrintReportMessage printReportMessage = reportGeneratorProbe.expectMsgClass(PrintReportMessage.class);
            assertEquals(RUNNERAMOUNT, printReportMessage.getRawResults().size());
            return null;
        });
    }
}