package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerResultMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class LinearTestRunnerTest {

    @Mock
    private LinearTestRunnerConfig mockTestRunnerConfig;

    private TestKit providerProbe;
    private TestKit testkit;
    private ActorRef sut;

    private static ActorSystem akkaSystem;

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
    private void setup(){
        when(mockTestRunnerConfig.getTxAmount()).thenReturn(10);
        when(mockTestRunnerConfig.getFunctionId()).thenReturn("YES");
        testkit = new TestKit(akkaSystem);
        providerProbe = new TestKit(akkaSystem);
        Props providerProps = Props.create(LinearTestRunner.class,mockTestRunnerConfig, providerProbe.getRef());
        sut =  akkaSystem.actorOf(providerProps);
    }

    @Test
    public void givenTestRunnerStartMessageThenGetResultMessage(){
        testkit.within(Duration.ofSeconds(10), () ->
        {
           sut.tell(new TestRunnerStartMessage(), testkit.getRef());
           List<Object> startMessages = providerProbe.receiveN(10);
            for (Object startMessage : startMessages) {
                sut.tell(new TransactionResult(), providerProbe.getRef());
            }
            TestRunnerResultMessage resultMessage = testkit.expectMsgClass(TestRunnerResultMessage.class);
            assertEquals(resultMessage.getResultList().size(), 10);
            return null;
        });
    };

}