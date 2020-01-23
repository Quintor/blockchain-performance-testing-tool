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
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumBlockchainConfig;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
public class EthereumBlockchainProviderTest {

    private static final Duration timeout = Duration.ofMinutes(1);
    private static final String DEFAULT_FUNCTIONID = "giveRightToVote";
    private static final int TXAMOUNT = 5;

    private EthereumBlockchainConfig ethereumBlockchainConfig;

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
        ethereumBlockchainConfig = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("testBlockchainProviderConfigBlock.yml"),
                EthereumBlockchainConfig.class);
        testkit = new TestKit(actorSystem);
        sut = actorSystem.actorOf(Props.create(EthereumBlockchainProvider.class, ethereumBlockchainConfig));
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
            sut.tell(new SendTransactionMessage(DEFAULT_FUNCTIONID), testkit.getRef());
            testkit.expectMsgClass(TransactionResult.class);
            return null;
        });
    }
    @Test
    public void givenMultipleSendTransactionMessageThenGetMultpleTransactionResult() {
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
