package nl.quintor.blockchain.ptt;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerTerminateMessage;
import nl.quintor.blockchain.ptt.cli.CLIParser;
import nl.quintor.blockchain.ptt.config.*;
import nl.quintor.blockchain.ptt.exceptions.SetupException;
import nl.quintor.blockchain.ptt.exceptions.TestInterruptedException;
import nl.quintor.blockchain.ptt.plugins.PTTPluginManager;
import nl.quintor.blockchain.ptt.plugins.exceptions.PluginStartException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceTestingToolApplicationTest {

    private static final String VALID_CONFIGFILEPATH = PerformanceTestingToolApplicationTest.class.getClassLoader().getResource("log4j2.yaml").getPath();
    private static final String INVALID_CONFIGFILEPATH = "INVALID_CONFIGFILEPATH";
    private static final String NULL_CONFIGFILEPATH = null;
    private static final OUTPUT_FORMAT DEFAULT_OUTPUTFORMAT = OUTPUT_FORMAT.PDF;
    private static final OUTPUT_FORMAT NONDEFAULT_OUTPUTFORMAT = OUTPUT_FORMAT.YAML;
    private static Map VALID_MAPCONFIG = new HashMap();
    private static Map INVALID_MAPCONFIG = new HashMap();
    private static final String VALID_YMLSTRING = "VALID_YMLSTRING";
    private static final String VALID_PROVIDERID = "VALID_PROVIDERID";
    private static final String VALID_PROVIDERTYPE = "VALID_PROVIDERTYPE";
    private static final String INVALID_PROVIDERTYPE = "INVALID_PROVIDERTYPE";
    private static final String VALID_PROVIDERCONFIG = "VALID_PROVIDERCONFIG";
    private static final String VALID_RUNNERID = "VALID_RUNNERID";
    private static final String VALID_RUNNERTYPE = "VALID_RUNNERTYPE";
    private static final String INVALID_RUNNERTYPE = "INVALID_RUNNERTYPE";


    @Mock
    private PTTPluginManager mockPluginManager;
    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private Yaml mockYaml;
    @Mock
    private CLIParser mockParser;

    @Mock
    private ApplicationConfig mockApplicationConfig;
    @Mock
    private TestScenarioConfig mockTestBaseConfig;

    private HashMap<String, RunnerConfig> mockRunnerConfigMap = new HashMap<>();
    @Mock
    private RunnerConfig mockRunnerConfig;

    private HashMap<String, ProviderConfig> mockProviderConfigMap = new HashMap<>();
    @Mock
    private ProviderConfig mockProviderConfig;

    private Props mockProvider;

    private Props mockRunner;

    private HashMap<String, SetupProviderConfig> mockUseProviderForSetupMap = new HashMap<>();

    @Mock
    private SetupProviderConfig mockSetupProviderConfig;

    @Mock
    private ActorSystem mockSystem;

    private static ActorSystem akkaSystem;

    private TestKit mockActor;

    private PerformanceTestingToolApplication sut;

    @AfterEach
    public  void teardown() {
        akkaSystem.terminate();
        sut = null;
    }

    @BeforeEach
    private void setup() {
        akkaSystem = spy(ActorSystem.create());
        mockProvider = Props.empty();
        mockRunner = Props.empty();
        mockActor = new TestKit(akkaSystem);
        mockRunnerConfigMap.put(VALID_RUNNERID, mockRunnerConfig);
        mockProviderConfigMap.put(VALID_PROVIDERID, mockProviderConfig);
        VALID_MAPCONFIG.put("Test", "TEst");
    }

    @Test
    public void givenInvalidArgumentThenThrowSetupException() throws ParseException, SetupException {
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        when(mockParser.getApplicationConfig()).thenThrow(new ParseException("Parse failed"));
        assertThrows(SetupException.class, () -> sut.setup());
    }

    @Test
    public void givenInvalidConfigPathThenThrowSetupException() throws ParseException, SetupException {
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(INVALID_CONFIGFILEPATH);
        assertThrows(SetupException.class, () -> sut.setup());
    }

    @Test
    public void givenNullConfigPathThenThrowSetupException() throws ParseException, SetupException {
        Yaml yaml = new Yaml();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, objectMapper, yaml, mockParser);
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(NULL_CONFIGFILEPATH);
        assertThrows(SetupException.class, () -> sut.setup());
    }

    @Test
    public void givenValidConfigPathButInvalidYamlThenThrowSetupException() throws ParseException, SetupException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, objectMapper, mockYaml, mockParser);
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(INVALID_MAPCONFIG);
        assertThrows(SetupException.class, () -> sut.setup());
    }

    @Test
    public void givenValidConfigThenNameIsSetInRunnerConfig() throws SetupException, ParseException, IOException, PluginStartException {
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockApplicationConfig.getOutputFormat()).thenReturn(DEFAULT_OUTPUTFORMAT);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(VALID_MAPCONFIG);
        when(mockObjectMapper.writeValueAsString(VALID_MAPCONFIG)).thenReturn(VALID_YMLSTRING);
        when(mockObjectMapper.readValue(VALID_YMLSTRING, TestScenarioConfig.class)).thenReturn(mockTestBaseConfig);
        when(mockTestBaseConfig.getRunners()).thenReturn(mockRunnerConfigMap);
        when(mockTestBaseConfig.getProviders()).thenReturn(mockProviderConfigMap);
        when(mockRunnerConfig.getProviderId()).thenReturn(VALID_PROVIDERID);
        when(mockObjectMapper.writeValueAsString(mockProviderConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockProviderConfig.getType()).thenReturn(VALID_PROVIDERTYPE);
        when(mockRunnerConfig.getType()).thenReturn(VALID_RUNNERTYPE);
        when(mockPluginManager.getBlockchainProvider(VALID_PROVIDERTYPE, VALID_PROVIDERCONFIG)).thenReturn(mockProvider);
        when(mockSystem.actorOf(mockProvider, VALID_PROVIDERID)).thenReturn(mockActor.getRef());
        when(mockObjectMapper.writeValueAsString(mockRunnerConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockPluginManager.getTestRunner(VALID_RUNNERTYPE, VALID_PROVIDERCONFIG, mockActor.getRef())).thenReturn(mockRunner);
        when(mockSystem.actorOf(mockRunner, VALID_RUNNERID)).thenReturn(mockActor.getRef());
        when(mockTestBaseConfig.getUseProvidersForSetup()).thenReturn(null);

        sut = new PerformanceTestingToolApplication(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertDoesNotThrow(() -> sut.setup());
        verify(mockRunnerConfig).setValues("name", VALID_RUNNERID);
    }
    
    @Test
    public void givenNonDefaultOutputFormatThenNoError() throws SetupException, ParseException, IOException, PluginStartException {
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockApplicationConfig.getOutputFormat()).thenReturn(NONDEFAULT_OUTPUTFORMAT);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(VALID_MAPCONFIG);
        when(mockObjectMapper.writeValueAsString(VALID_MAPCONFIG)).thenReturn(VALID_YMLSTRING);
        when(mockObjectMapper.readValue(VALID_YMLSTRING, TestScenarioConfig.class)).thenReturn(mockTestBaseConfig);
        when(mockTestBaseConfig.getRunners()).thenReturn(mockRunnerConfigMap);
        when(mockTestBaseConfig.getProviders()).thenReturn(mockProviderConfigMap);
        when(mockRunnerConfig.getProviderId()).thenReturn(VALID_PROVIDERID);
        when(mockObjectMapper.writeValueAsString(mockProviderConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockProviderConfig.getType()).thenReturn(VALID_PROVIDERTYPE);
        when(mockRunnerConfig.getType()).thenReturn(VALID_RUNNERTYPE);
        when(mockPluginManager.getBlockchainProvider(VALID_PROVIDERTYPE, VALID_PROVIDERCONFIG)).thenReturn(mockProvider);
        when(mockSystem.actorOf(mockProvider, VALID_PROVIDERID)).thenReturn(mockActor.getRef());
        when(mockObjectMapper.writeValueAsString(mockRunnerConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockPluginManager.getTestRunner(VALID_RUNNERTYPE, VALID_PROVIDERCONFIG, mockActor.getRef())).thenReturn(mockRunner);
        when(mockSystem.actorOf(mockRunner, VALID_RUNNERID)).thenReturn(mockActor.getRef());
        when(mockTestBaseConfig.getUseProvidersForSetup()).thenReturn(null);

        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertDoesNotThrow(() -> sut.setup());
    }
    

    @Test
    public void givenNoUseProvidersForSetupThenNoSetup() throws SetupException, ParseException, IOException, PluginStartException {
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockApplicationConfig.getOutputFormat()).thenReturn(DEFAULT_OUTPUTFORMAT);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(VALID_MAPCONFIG);
        when(mockObjectMapper.writeValueAsString(VALID_MAPCONFIG)).thenReturn(VALID_YMLSTRING);
        when(mockObjectMapper.readValue(VALID_YMLSTRING, TestScenarioConfig.class)).thenReturn(mockTestBaseConfig);
        when(mockTestBaseConfig.getRunners()).thenReturn(mockRunnerConfigMap);
        when(mockTestBaseConfig.getProviders()).thenReturn(mockProviderConfigMap);
        when(mockRunnerConfig.getProviderId()).thenReturn(VALID_PROVIDERID);
        when(mockObjectMapper.writeValueAsString(mockProviderConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockProviderConfig.getType()).thenReturn(VALID_PROVIDERTYPE);
        when(mockRunnerConfig.getType()).thenReturn(VALID_RUNNERTYPE);
        when(mockPluginManager.getBlockchainProvider(VALID_PROVIDERTYPE, VALID_PROVIDERCONFIG)).thenReturn(mockProvider);
        when(mockSystem.actorOf(mockProvider, VALID_PROVIDERID)).thenReturn(mockActor.getRef());
        when(mockObjectMapper.writeValueAsString(mockRunnerConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockPluginManager.getTestRunner(VALID_RUNNERTYPE, VALID_PROVIDERCONFIG, mockActor.getRef())).thenReturn(mockRunner);
        when(mockSystem.actorOf(mockRunner, VALID_RUNNERID)).thenReturn(mockActor.getRef());
        when(mockTestBaseConfig.getUseProvidersForSetup()).thenReturn(null);

        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertDoesNotThrow(() -> sut.setup());
    }

    @Test
    public void givenInvalidProviderTypeThenThrowSetupException() throws SetupException, ParseException, IOException, PluginStartException {
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(VALID_MAPCONFIG);
        when(mockObjectMapper.writeValueAsString(VALID_MAPCONFIG)).thenReturn(VALID_YMLSTRING);
        when(mockObjectMapper.readValue(VALID_YMLSTRING, TestScenarioConfig.class)).thenReturn(mockTestBaseConfig);
        when(mockTestBaseConfig.getRunners()).thenReturn(mockRunnerConfigMap);
        when(mockTestBaseConfig.getProviders()).thenReturn(mockProviderConfigMap);
        when(mockRunnerConfig.getProviderId()).thenReturn(VALID_PROVIDERID);
        when(mockObjectMapper.writeValueAsString(mockProviderConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockProviderConfig.getType()).thenReturn(INVALID_PROVIDERTYPE);
        when(mockPluginManager.getBlockchainProvider(INVALID_PROVIDERTYPE, VALID_PROVIDERCONFIG)).thenThrow(new PluginStartException("Couldn't find plugin"));

        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertThrows(SetupException.class, () -> sut.setup());
    }

    @Test
    public void givenInvalidRunnerTypeThenThrowSetupException() throws SetupException, ParseException, IOException, PluginStartException {
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(VALID_MAPCONFIG);
        when(mockObjectMapper.writeValueAsString(VALID_MAPCONFIG)).thenReturn(VALID_YMLSTRING);
        when(mockObjectMapper.readValue(VALID_YMLSTRING, TestScenarioConfig.class)).thenReturn(mockTestBaseConfig);
        when(mockTestBaseConfig.getRunners()).thenReturn(mockRunnerConfigMap);
        when(mockTestBaseConfig.getProviders()).thenReturn(mockProviderConfigMap);
        when(mockRunnerConfig.getProviderId()).thenReturn(VALID_PROVIDERID);
        when(mockObjectMapper.writeValueAsString(mockProviderConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockProviderConfig.getType()).thenReturn(VALID_PROVIDERTYPE);
        when(mockRunnerConfig.getType()).thenReturn(INVALID_RUNNERTYPE);
        when(mockPluginManager.getBlockchainProvider(VALID_PROVIDERTYPE, VALID_PROVIDERCONFIG)).thenReturn(mockProvider);
        when(mockSystem.actorOf(mockProvider, VALID_PROVIDERID)).thenReturn(mockActor.getRef());
        when(mockObjectMapper.writeValueAsString(mockRunnerConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockPluginManager.getTestRunner(INVALID_RUNNERTYPE, VALID_PROVIDERCONFIG, mockActor.getRef())).thenThrow(new PluginStartException("Couldn't find plugin"));
//        when(mockSystem.actorOf(mockRunner, VALID_RUNNERID)).thenReturn(mockActor.getRef());

        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertThrows(SetupException.class, () -> sut.setup());
    }

    @Test
    public void givenUseProvidersForSetupThenSetup() throws SetupException, ParseException, IOException, PluginStartException {
        when(mockParser.getApplicationConfig()).thenReturn(mockApplicationConfig);
        when(mockApplicationConfig.getFilePath()).thenReturn(VALID_CONFIGFILEPATH);
        when(mockYaml.load(any(FileInputStream.class))).thenReturn(VALID_MAPCONFIG);
        when(mockObjectMapper.writeValueAsString(VALID_MAPCONFIG)).thenReturn(VALID_YMLSTRING);
        when(mockObjectMapper.readValue(VALID_YMLSTRING, TestScenarioConfig.class)).thenReturn(mockTestBaseConfig);
        when(mockTestBaseConfig.getRunners()).thenReturn(mockRunnerConfigMap);
        when(mockTestBaseConfig.getProviders()).thenReturn(mockProviderConfigMap);
        when(mockRunnerConfig.getProviderId()).thenReturn(VALID_PROVIDERID);
        when(mockObjectMapper.writeValueAsString(mockProviderConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockProviderConfig.getType()).thenReturn(VALID_PROVIDERTYPE);
        when(mockRunnerConfig.getType()).thenReturn(VALID_RUNNERTYPE);
        when(mockPluginManager.getBlockchainProvider(VALID_PROVIDERTYPE, VALID_PROVIDERCONFIG)).thenReturn(mockProvider);
        when(mockSystem.actorOf(mockProvider, VALID_PROVIDERID)).thenReturn(mockActor.getRef());
        when(mockObjectMapper.writeValueAsString(mockRunnerConfig.getValues())).thenReturn(VALID_PROVIDERCONFIG);
        when(mockPluginManager.getTestRunner(VALID_RUNNERTYPE, VALID_PROVIDERCONFIG, mockActor.getRef())).thenReturn(mockRunner);
        when(mockSystem.actorOf(mockRunner, VALID_RUNNERID)).thenReturn(mockActor.getRef());
        when(mockTestBaseConfig.getUseProvidersForSetup()).thenReturn(mockUseProviderForSetupMap);
        when(mockApplicationConfig.getOutputFormat()).thenReturn(DEFAULT_OUTPUTFORMAT);

        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertDoesNotThrow(() -> sut.setup());
    }

    @Test
    public void givenStartWithoutSetupThenThrowSetupExpection() throws SetupException {
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, mockSystem, mockObjectMapper, mockYaml, mockParser);
        assertThrows(SetupException.class, () -> sut.start());
    }

    @Test
    public void givenStartThenTestRunnerGetsStartMessage() throws SetupException {
        TestKit mockTestRunnerManager = new TestKit(akkaSystem);
        TestKit testkit = new TestKit(akkaSystem);
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, akkaSystem, mockObjectMapper, mockYaml, mockParser);
        testkit.within(Duration.ofSeconds(3), () -> {
                new Thread(() -> {
                    try {
                        sut.start(mockTestRunnerManager.getRef(), mockApplicationConfig);
                    } catch (TestInterruptedException e) {
                        e.printStackTrace();
                    } catch (SetupException e) {
                        e.printStackTrace();
                    }
                }).start();
                mockTestRunnerManager.expectMsgClass(TestRunnerStartMessage.class);
                mockTestRunnerManager.getSystem().terminate();
            return null;
        });
    }
    @Test
    public void givenTimeoutThenTestRunnerGetsTerminateMessage() throws SetupException {
        when(mockApplicationConfig.getTimeout()).thenReturn(Duration.ofSeconds(1));
        TestKit mockTestRunnerManager = new TestKit(akkaSystem);
        TestKit testkit = new TestKit(akkaSystem);
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, akkaSystem, mockObjectMapper, mockYaml, mockParser);
        testkit.within(Duration.ofSeconds(3), () -> {
            new Thread(() -> {
                try {
                    sut.start(mockTestRunnerManager.getRef(), mockApplicationConfig);
                } catch (TestInterruptedException e) {
                    e.printStackTrace();
                } catch (SetupException e) {
                    e.printStackTrace();
                }
            }).start();
            mockTestRunnerManager.expectMsgClass(TestRunnerStartMessage.class);
            mockTestRunnerManager.expectMsgClass(TestRunnerTerminateMessage.class);
            akkaSystem.terminate();
            return null;
        });
    }


    @Test
    public void givenTestInterruptThenTerminateMessageIsSend() throws SetupException {
        TestKit mockTestRunnerManager = new TestKit(akkaSystem);
        TestKit testkit = new TestKit(akkaSystem);
        sut = PerformanceTestingToolApplication.construct(mockPluginManager, akkaSystem, mockObjectMapper, mockYaml, mockParser);
        testkit.within(Duration.ofSeconds(3), () -> {
            Thread thread = new Thread(() -> assertThrows(TestInterruptedException.class, () ->sut.start(mockTestRunnerManager.getRef(), mockApplicationConfig)));
            thread.start();
            mockTestRunnerManager.expectMsgClass(TestRunnerStartMessage.class);
            thread.interrupt();
            mockTestRunnerManager.expectMsgClass(TestRunnerTerminateMessage.class);
            akkaSystem.terminate();
            return null;
        });
    }

//    Commented because it sometimes fails and sometimes passes without changing anything needs to be reworked
//    @Test
//    public void givenTestInterruptWithoutGracefulShutdownThenTerminateGetsCalled() throws SetupException {
//        TestKit mockTestRunnerManager = new TestKit(akkaSystem);
//        TestKit testkit = new TestKit(akkaSystem);
//        sut = PerformanceTestingToolApplication.construct(mockPluginManager, akkaSystem, mockObjectMapper, mockYaml, mockParser);
//        testkit.within(Duration.ofSeconds(3), () -> {
//            Thread thread = new Thread(() -> assertThrows(TestInterruptedException.class, () ->sut.start(mockTestRunnerManager.getRef(), mockApplicationConfig)));
//            thread.start();
//            mockTestRunnerManager.expectMsgClass(TestRunnerStartMessage.class);
//            thread.interrupt();
//            mockTestRunnerManager.expectMsgClass(TestRunnerTerminateMessage.class);
//            thread.interrupt();
//            verify(akkaSystem).terminate();
//            return null;
//        });
//    }
}