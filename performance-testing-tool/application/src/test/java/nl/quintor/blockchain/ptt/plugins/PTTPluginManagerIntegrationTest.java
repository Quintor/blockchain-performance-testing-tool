package nl.quintor.blockchain.ptt.plugins;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import nl.quintor.blockchain.ptt.plugins.exceptions.PluginStartException;
import org.junit.jupiter.api.*;
import org.pf4j.DefaultPluginManager;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PTTPluginManagerIntegrationTest {

    private static ActorSystem actorSystem;

    private final DefaultPluginManager PF4F_PLUGIN_MANAGER = new DefaultPluginManager();

    private final String BLOCKCHAINPROVIDER_PLUGINID = "ethereum-blockchainprovider";
    private String defaultBlockchainConfigBlock;
    private final String TESTRUNNER_PLUGINID = "linear-testrunner";
    private String defaultTestRunnerConfigBlock;
//    private final String DEFAULT_TESTRUNNER_CONFIG;

    private PTTPluginManager sut;

    @BeforeAll
    private static void setupActorSystem(){
        actorSystem = ActorSystem.create();
    }

    @AfterAll
    private static void teardownActorSystem(){
        actorSystem.terminate();
    }

    @BeforeEach
    private void setup() throws IOException {
        defaultBlockchainConfigBlock = loadYamlAsString("blockchainProviderConfigBlock.yml");
        defaultTestRunnerConfigBlock = loadYamlAsString("testRunnerConfigBlock.yml");
        sut = new PTTPluginManager(PF4F_PLUGIN_MANAGER);

    }

    @Test
    public void testLoadingBothPlugins(){
       assertDoesNotThrow(() ->{
            Props blockchainProvider = sut.getBlockchainProvider(BLOCKCHAINPROVIDER_PLUGINID, defaultBlockchainConfigBlock);
            ActorRef blockchainProviderActor = actorSystem.actorOf(blockchainProvider);
            Props testRunner = sut.getTestRunner(TESTRUNNER_PLUGINID, defaultTestRunnerConfigBlock, blockchainProviderActor);
            actorSystem.actorOf(blockchainProvider);
       });
    }

    @Test
    public void givenLoadBlockchainPluginWithValidTestRunnerPluginIdGetPluginStartException(){
        assertThrows(PluginStartException.class, () ->{
            Props blockchainProvider = sut.getBlockchainProvider(TESTRUNNER_PLUGINID, defaultBlockchainConfigBlock);
            ActorRef blockchainProviderActor = actorSystem.actorOf(blockchainProvider);
        });
    }

    private String loadYamlAsString(String location) {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
        Map<String, Object> loaded = (Map<String, Object>) yaml.load(inputStream);
        return yaml.dump(loaded);
    }
}
