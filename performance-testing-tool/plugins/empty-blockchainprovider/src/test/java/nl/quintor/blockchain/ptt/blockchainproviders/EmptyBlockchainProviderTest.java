package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmptyBlockchainProviderTest {
    private static final Duration timeout = Duration.ofMinutes(1);
    private static final String DEFAULT_FUNCTIONID = "function1";
    private static final int TXAMOUNT = 5;

    private EmptyBlockchainProviderConfig emptyBlockchainConfig;

    private static ActorSystem actorSystem;
    private static ObjectMapper objectMapper;

    private ActorRef sut;
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
        emptyBlockchainConfig = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("testBlockchainProviderConfigBlock.yml"),
                                                        EmptyBlockchainProviderConfig.class);
        testkit = new TestKit(actorSystem);
        sut = actorSystem.actorOf(Props.create(EmptyBlockchainProvider.class, emptyBlockchainConfig));
    }
    @Test
    public void givenSetupNetworkMessageThenGetSetupMessageWithConfigString(){
        testkit.within(timeout, () -> {
            sut.tell(new SetupNetworkMessage(), testkit.getRef());
            SetupNetworkMessage message = testkit.expectMsgClass(SetupNetworkMessage.class);
            assertNotNull(message.getSetupConfigString());
            return null;
        });
    }
    @Test
    public void givenSendTransactionMessageThenGetTransactionResult(){
        testkit.within(timeout, () -> {
            for (int i = 0; i < TXAMOUNT; i++) {
                sut.tell(new SendTransactionMessage(DEFAULT_FUNCTIONID), testkit.getRef());
            }
            List<Object> results = testkit.receiveN(5);
            for (Object result : results) {
                assertEquals(result.getClass(), TransactionResult.class);
            }
            return null;
        });
    }
}
