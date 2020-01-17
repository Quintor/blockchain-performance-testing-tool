package nl.quintor.blockchain.ptt.parser;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.parser.messages.ParseMessage;
import nl.quintor.blockchain.ptt.parser.messages.ParseResultMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionResultParserTest {


    public static final String DEFAULT_TEST_BEGINNING_TIME = "2019-11-14T10:21:15.216035Z";
    private static ActorSystem akkaSystem;

    private List<TransactionResult> defaultRawResults;
    private ParsedMetrics defaultParsedResults;

    private ActorRef sut;
    private TestKit testkit;

    @BeforeAll
    public static void setupSystem() {
        akkaSystem = ActorSystem.create();
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(akkaSystem);
        akkaSystem = null;
    }

    @BeforeEach
    private void setup() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        defaultRawResults = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("rawTransactionResults.yml"), new TypeReference<List<TransactionResult>>() {
        });
        defaultParsedResults = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("parsedTransactionResults.yml"), new TypeReference<ParsedMetrics>() {
        });
        final Props props = Props.create(TransactionResultParser.class);
        sut = akkaSystem.actorOf(props);
        testkit = new TestKit(akkaSystem);
    }
    @Test
    public void givenEmptyRawResultListThenNullParsedMetricsFieldIsReturned() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), new ArrayList<>()), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertNull(message.getParsedMetrics());
                    return null;
                });
    }
    @Test
    public void givenNullRawResultListThenNullParsedMetricsFieldIsReturned() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), null), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertNull(message.getParsedMetrics());
                    return null;
                });
    }

    @Test
    public void givenDefaultRawResultsThenTransactionsPerSecondIsTheSame() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), defaultRawResults), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertAll(
                            () -> assertEquals(defaultParsedResults.getTransactionsPerSecond(), message.getParsedMetrics().getTransactionsPerSecond())
                    );
                    return null;
                });
    }

    @Test
    public void givenDefaultRawResultsThenSuccessRatesAreTheSame() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), defaultRawResults), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertAll(
                            () -> assertEquals(defaultParsedResults.getReceiveSuccesRate(), message.getParsedMetrics().getReceiveSuccesRate()),
                            () -> assertEquals(defaultParsedResults.getConfirmSuccesRate(), message.getParsedMetrics().getConfirmSuccesRate())
                    );
                    return null;
                });
    }

    @Test
    public void givenDefaultRawResultsThenResponseTimeListsAreTheSame() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), defaultRawResults), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertAll(
                            () -> assertEquals(defaultParsedResults.getResponseTimesToReceive(), message.getParsedMetrics().getResponseTimesToReceive()),
                            () -> assertEquals(defaultParsedResults.getResponseTimesToConfirm(), message.getParsedMetrics().getResponseTimesToConfirm())
                    );
                    return null;
                });
    }

    @Test
    public void givenDefaultRawResultsThenBeginAndEndTimesAreTheSame() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), defaultRawResults), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertAll(
                            () -> assertEquals(defaultParsedResults.getBeginTime(), message.getParsedMetrics().getBeginTime()),
                            () -> assertEquals(defaultParsedResults.getEndTime(), message.getParsedMetrics().getEndTime())
                    );
                    return null;
                });
    }

    @Test
    public void givenDefaultRawResultsThenErrorMapIsTheSame() {
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), defaultRawResults), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertAll(
                            () -> assertEquals(defaultParsedResults.getErrorMap().keySet(), message.getParsedMetrics().getErrorMap().keySet(), "Error Map, same errors")
                    );
                    for (Map.Entry<String, TransactionErrorCount> errorCount : defaultParsedResults.getErrorMap().entrySet()) {
                        assertEquals(errorCount.getValue().getCount(), message.getParsedMetrics().getErrorMap().get(errorCount.getKey()).getCount());
                        assertEquals(errorCount.getValue().getError().getTime(), message.getParsedMetrics().getErrorMap().get(errorCount.getKey()).getError().getTime());
                        assertEquals(errorCount.getValue().getError().getMessage(), message.getParsedMetrics().getErrorMap().get(errorCount.getKey()).getError().getMessage());
                    }
                    return null;
                });
    }

    @Test
    public void givenDefaultRawResultsThenStatusTimelineIsTheSame(){
        testkit.within(
                Duration.ofSeconds(10),
                () ->
                {
                    sut.tell(new ParseMessage(Instant.parse(DEFAULT_TEST_BEGINNING_TIME), defaultRawResults), testkit.getRef());
                    ParseResultMessage message = testkit.expectMsgClass(ParseResultMessage.class);
                    assertEquals(defaultParsedResults.getStatusTimeline().keySet(), message.getParsedMetrics().getStatusTimeline().keySet());
                    for (Map.Entry<Instant, StatusOverviewTimelineSnapshot> timeline : defaultParsedResults.getStatusTimeline().entrySet()){
                        assertEquals(timeline.getValue().getAmountSend(), message.getParsedMetrics().getStatusTimeline().get(timeline.getKey()).getAmountSend());
                        assertEquals(timeline.getValue().getAmountReceived(), message.getParsedMetrics().getStatusTimeline().get(timeline.getKey()).getAmountReceived());
                        assertEquals(timeline.getValue().getAmountConfirmed(), message.getParsedMetrics().getStatusTimeline().get(timeline.getKey()).getAmountConfirmed());
                        assertEquals(timeline.getValue().getAmountFailed(), message.getParsedMetrics().getStatusTimeline().get(timeline.getKey()).getAmountFailed());
                    }
                    return null;
                });
    }
}