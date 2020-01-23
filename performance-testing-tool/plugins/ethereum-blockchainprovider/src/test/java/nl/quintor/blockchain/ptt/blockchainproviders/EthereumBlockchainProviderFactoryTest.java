package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumBlockchainConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class EthereumBlockchainProviderFactoryTest {

    private static final String INVALIDBLOCKCHAINCONFIG = "INVALID";
    private static final String VALIDBLOCKCHAINCONFIG = "VALID";

    @Mock
    private EthereumBlockchainConfig mockBlockChainConfig;

    @Mock
    private ObjectMapper mockMapper;

    @Mock
    private Logger mockLogger;


    @InjectMocks
    private EthereumBlockchainProviderFactory sut;

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
    void setup() throws IOException {
        initMocks(this);
    }

    @Tag("integration")
    @Test
    public void givenCreatInstanceBlockchainConfigIsFullConfigThenBlockchainProviderIsReturned() throws BlockchainConfigException, IOException {
        sut.objectMapper = new ObjectMapper(new YAMLFactory());
        String configString = Files.readString(Paths.get(this.getClass().getClassLoader().getResource("fullConfig.yml").getPath()));
        assertEquals(Props.class, sut.createInstance(configString).getClass());
    }

    @Test
    public void givenCreatInstanceBlockchainConfigIsValidThenBlockchainProviderIsReturned() throws BlockchainConfigException, IOException {
        when(mockMapper.readValue(VALIDBLOCKCHAINCONFIG, EthereumBlockchainConfig.class)).thenReturn(mockBlockChainConfig);
        assertEquals(Props.class, sut.createInstance(VALIDBLOCKCHAINCONFIG).getClass());
    }

    @Test
    public void givenCreateInstanceBlockchainConfigIsNullThrowNewBlockchainConfigException(){
        assertThrows(BlockchainConfigException.class, () -> sut.createInstance(null));
    }

    @Test
    public void givenCreateInstanceBlockchainConfigIsInvalidThrowNewBlockchainConfigException() throws IOException {
        when(mockMapper.readValue(INVALIDBLOCKCHAINCONFIG, EthereumBlockchainConfig.class)).thenThrow(new IOException());
        assertThrows(BlockchainConfigException.class, () -> sut.createInstance(INVALIDBLOCKCHAINCONFIG));
    }
}