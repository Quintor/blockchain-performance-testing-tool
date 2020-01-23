package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerResultMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerTerminateMessage;
import nl.quintor.blockchain.ptt.blockchainproviders.MockProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmptyTestRunnerTest {
    private static final Duration timeout = Duration.ofMinutes(1);

    private EmptyTestRunnerConfig emptyTestRunnerConfig;

    private static ActorSystem actorSystem;
    private static ObjectMapper objectMapper;

    private ActorRef sut;
    private ActorRef mockProvider;
    private TestKit testkit;

    @BeforeAll
    public static void setupSystem() {
        actorSystem = ActorSystem.create();
        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }

    @BeforeEach
    private void setup() throws IOException {
        emptyTestRunnerConfig = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("testRunnerConfigBlock.yml"),
                EmptyTestRunnerConfig.class);
        testkit = new TestKit(actorSystem);
        mockProvider = actorSystem.actorOf(Props.create(MockProvider.class, Duration.ofSeconds(1), Duration.ofSeconds(1), 0.5d));
        sut = actorSystem.actorOf(Props.create(EmptyTestRunner.class, emptyTestRunnerConfig, mockProvider));
    }

    @Test
    public void givenTestRunnerStartMessageThenGetTestRunnerResultMessage(){
        testkit.within(timeout, () -> {
            sut.tell(new TestRunnerStartMessage(), testkit.getRef());
            TestRunnerResultMessage message = testkit.expectMsgClass(TestRunnerResultMessage.class);
            assertNotNull(message.getResultList());
            return null;
        });
    }

    @Test
    public void givenTestRunnerTerminateMessageThenGetOneTestRunnerResultMessage(){
        testkit.within(timeout, () -> {
            sut.tell(new TestRunnerStartMessage(), testkit.getRef());
            sut.tell(new TestRunnerTerminateMessage(), testkit.getRef());
            List<Object> message = testkit.receiveN(1);
            for (Object result : message) {
                assertEquals(result.getClass(), TestRunnerResultMessage.class);
            }
            testkit.expectNoMessage(Duration.ofSeconds(5));
            return null;
        });
    }
}