package nl.quintor.blockchain.ptt;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerTerminateMessage;
import nl.quintor.blockchain.ptt.cli.CLIParser;
import nl.quintor.blockchain.ptt.config.*;
import nl.quintor.blockchain.ptt.exceptions.SetupException;
import nl.quintor.blockchain.ptt.exceptions.TestInterruptedException;
import nl.quintor.blockchain.ptt.parser.TransactionResultParser;
import nl.quintor.blockchain.ptt.plugins.PTTPluginManager;
import nl.quintor.blockchain.ptt.plugins.exceptions.PluginStartException;
import nl.quintor.blockchain.ptt.report.YAMLReportGenerator;
import nl.quintor.blockchain.ptt.report.pdf.PDFReportGenerator;
import nl.quintor.blockchain.ptt.testrunners.TestRunnerManager;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static akka.pattern.Patterns.ask;

public class PerformanceTestingToolApplication {

    private final Logger logger = LoggerFactory.getLogger(PerformanceTestingToolApplication.class);
    private PTTPluginManager pluginManager;
    private ActorSystem actorSystem;
    private ObjectMapper objectMapper;
    private Yaml yaml;
    private CLIParser cliParser;
    private ActorRef testRunnerManager;
    private ApplicationConfig applicationConfig;
    private TestScenarioConfig testScenarioConfig;

    public PerformanceTestingToolApplication(PTTPluginManager pluginManager, ActorSystem actorSystem, ObjectMapper objectMapper, Yaml yaml, CLIParser cliParser) throws SetupException {
        if(pluginManager ==null || actorSystem == null || objectMapper == null || yaml == null || cliParser == null){
            throw new SetupException("Invalid setup objects");
        }
        this.pluginManager = pluginManager;
        this.actorSystem = actorSystem;
        this.objectMapper = objectMapper;
        this.yaml = yaml;
        this.cliParser = cliParser;
    }

    public void setup() throws SetupException {
        try {
            applicationConfig = cliParser.getApplicationConfig();
        } catch (ParseException e) {
            throw new SetupException("Error parsing arguments", e);
        }

        setTestScenarioConfig(applicationConfig.getFilePath());

        List<ActorRef> testRunnerList = setupTestRunners();
        this.testRunnerManager = setupTestRunnerManager(testRunnerList);
    }

    public void start() throws SetupException, TestInterruptedException {
        if(testRunnerManager == null){
            throw new SetupException("Application isn't setup yet");
        }
        testRunnerManager.tell(new TestRunnerStartMessage(), null);
        if (applicationConfig.getTimeout() != null) {
            actorSystem.scheduler().scheduleOnce(applicationConfig.getTimeout(), testRunnerManager, new TestRunnerTerminateMessage(), actorSystem.getDispatcher(), null);
        }
        try {
            actorSystem.getWhenTerminated().toCompletableFuture().get();
            logger.info("Testing done");
        } catch (InterruptedException | ExecutionException e) {
            try {
                testRunnerManager.tell( new TestRunnerTerminateMessage(), null);
                actorSystem.getWhenTerminated().toCompletableFuture().get();
                logger.info("Graceful shutdown succesful report has been generated");
            } catch (InterruptedException | ExecutionException ex) {
                actorSystem.terminate();
                Thread.currentThread().interrupt();
                throw new TestInterruptedException("Testing failed forced shutdown");
            }
            Thread.currentThread().interrupt();
            throw new TestInterruptedException("Testing failed gracefully shutdown");
        }
    }

    /*
        The unofficial merge key (<<, https://yaml.org/type/merge.html) doesn't work with Jackson, but it does work with the underlying library SnakeYaml
        That's why this function uses the SnakeYaml object Yaml to read the config file.
        Which gives us a Map with all the values on the merge keys (Jackson would read the key as a value instead of duplicating the correct value)
        Then we use Jackson to write the value back into a yml string so jackson can read this value again to create
        an object of TestBaseConfig with all the values set according to the merge key.
        SnakeYaml is not used to write the values to a string containing valid yml because SnakeYaml automaticly generates yaml anchors and references,
        Jackson can't parse these for some reason and can't be turned off easily.
        This way using duplicate values in the config file is a lot easier because you can override values when using the merge key.
     */
    private void setTestScenarioConfig(String configFilePath) throws SetupException {
        try {
            Map mapConfig = (Map) yaml.load(new FileInputStream(configFilePath));
            if (mapConfig == null || mapConfig.size() == 0) {
                throw new FileNotFoundException("Yaml couldn't be found");
            }
            String dump = objectMapper.writeValueAsString(mapConfig);
            testScenarioConfig = objectMapper.readValue(dump, TestScenarioConfig.class);
            if (testScenarioConfig == null) throw new InvalidObjectException("Failed to parse yml to Object");
        } catch (NullPointerException | IOException e) {
            throw new SetupException("Failed to read Config file", e);
        }
    }

    private List<ActorRef> setupTestRunners() throws SetupException {
        List<ActorRef> testRunnerList = new ArrayList<>();
        Map<String, ActorRef> providers = new HashMap<>();
        for (Map.Entry<String, RunnerConfig> entry : testScenarioConfig.getRunners().entrySet()) {
            RunnerConfig runnerConfig = entry.getValue();
            runnerConfig.setValues("name", entry.getKey());
            if(!testScenarioConfig.getProviders().containsKey(runnerConfig.getProviderId())) {
                throw new SetupException(String.format("Provider %s linked in configuration of Runner %s does not exist", runnerConfig.getProviderId(), entry.getKey()));
            }
            ProviderConfig providerConfig = testScenarioConfig.getProviders().get(runnerConfig.getProviderId());
            ActorRef provider;
            try {
                String providerConfigString = objectMapper.writeValueAsString(providerConfig.getValues());
                provider = actorSystem.actorOf(pluginManager.getBlockchainProvider(providerConfig.getType(), providerConfigString), runnerConfig.getProviderId());
                providers.put(runnerConfig.getProviderId(), provider);
                testRunnerList.add(actorSystem.actorOf(pluginManager.getTestRunner(runnerConfig.getType(), objectMapper.writeValueAsString(runnerConfig.getValues()), provider), entry.getKey()));
            } catch (PluginStartException | JsonProcessingException e) {
                throw new SetupException(String.format("Failed to start plugins %n %s", e.getMessage()));
            }
        }
        if (testScenarioConfig.getUseProvidersForSetup() != null) {
            setupBlockchainNetwork(providers);
        }
        return testRunnerList;
    }

    private void setupBlockchainNetwork(Map<String, ActorRef> providers) throws SetupException {
        try {
            for (Map.Entry<String, SetupProviderConfig> entry : testScenarioConfig.getUseProvidersForSetup().entrySet()) {
                SetupNetworkMessage setupResponse = (SetupNetworkMessage) ask(providers.get(entry.getKey()), new SetupNetworkMessage(), Duration.ofMinutes(2)).toCompletableFuture().get();
                if (entry.getValue() != null) {
                    for (String providerId : entry.getValue().getPassthroughSetupDataTo()) {
                        providers.get(providerId).tell(setupResponse, null);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Setup Providers failed shutting down");
            Thread.currentThread().interrupt();
            throw new SetupException("Setup Providers failed shutting down", e);
        }
    }

    private ActorRef setupTestRunnerManager(List<ActorRef> testRunnerList) throws SetupException {
        if(testRunnerList == null || testRunnerList.isEmpty()){
            throw new SetupException("There are no testrunners set up");
        }
        ActorRef reportGenerator;
        if(applicationConfig.getOutputFormat() == OUTPUT_FORMAT.PDF) {
            reportGenerator = actorSystem.actorOf(Props.create(PDFReportGenerator.class, applicationConfig.getOutput(), testScenarioConfig));
        }else if(applicationConfig.getOutputFormat() == OUTPUT_FORMAT.YAML){
            reportGenerator = actorSystem.actorOf(Props.create(YAMLReportGenerator.class, applicationConfig.getOutput(), testScenarioConfig));
        }else{
            throw new SetupException("No valid OutputFormat is set");
        }
        ActorRef metricParser = actorSystem.actorOf(Props.create(TransactionResultParser.class));
        return actorSystem.actorOf(Props.create(TestRunnerManager.class, testRunnerList, metricParser, reportGenerator));
    }

    public static PerformanceTestingToolApplication construct(PTTPluginManager pluginManager, ActorSystem actorSystem, ObjectMapper objectMapper, Yaml yaml, CLIParser cliParser) throws SetupException {
        if(pluginManager ==null || actorSystem == null || objectMapper == null || yaml == null || cliParser == null){
            throw new SetupException("Invalid setup objects");
        }
        return new PerformanceTestingToolApplication(pluginManager, actorSystem, objectMapper, yaml, cliParser);

    }

    void start(ActorRef testRunnerManager, ApplicationConfig applicationConfig) throws TestInterruptedException, SetupException {
        this.testRunnerManager = testRunnerManager;
        this.applicationConfig = applicationConfig;
        start();
    }
}
