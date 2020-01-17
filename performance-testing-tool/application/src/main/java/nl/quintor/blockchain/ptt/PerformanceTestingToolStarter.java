package nl.quintor.blockchain.ptt;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.quintor.blockchain.ptt.cli.CLIParser;
import nl.quintor.blockchain.ptt.cli.arguments.*;
import nl.quintor.blockchain.ptt.exceptions.SetupException;
import nl.quintor.blockchain.ptt.exceptions.TestInterruptedException;
import nl.quintor.blockchain.ptt.plugins.PTTPluginManager;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.pf4j.DefaultPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;


public class PerformanceTestingToolStarter {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestingToolStarter.class);
    private static final int FAILEDATSETUP = 1;
    private static final int FAILEDDURINGTESTING = 2;
    private static final int SUCCESS = 0;


    public static void main(String[] args) {
        PTTPluginManager pluginManager = new PTTPluginManager(new DefaultPluginManager());
        ActorSystem actorSystem = ActorSystem.create("PerformanceTestingTool");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        Yaml yaml = new Yaml();
        CLIParser cliParser = getCLIParser(args);

        try{
            PerformanceTestingToolApplication application = new PerformanceTestingToolApplication(pluginManager, actorSystem, objectMapper, yaml, cliParser);
            application.setup();
            application.start();
        }catch(SetupException e){
            logger.error("Failed at setting up application shutting down because \n {}", e.getMessage());
            System.exit(FAILEDATSETUP);
        }catch(TestInterruptedException e){
            logger.error("Failed to complete test because \n {}", e.getMessage());
            System.exit(FAILEDDURINGTESTING);
        }
        System.exit(SUCCESS);
    }

    private static CLIParser getCLIParser(String[] args) {
        OutputFormatCLIArgumentParser outputFormatCLIArgumentParser = new OutputFormatCLIArgumentParser();
        TimeOutCLIArgumentParser timeOutCLIArgumentParser = new TimeOutCLIArgumentParser(outputFormatCLIArgumentParser);
        OutputFileCLIArgumentParser outputFileCLIArgumentParser = new OutputFileCLIArgumentParser(timeOutCLIArgumentParser);
        CLIArgumentParser cliArgumentParserStartLink = new ConfigFileCLIArgumentParser(outputFileCLIArgumentParser);
        try {
            return new CLIParser(args, cliArgumentParserStartLink, new DefaultParser(), new HelpFormatter());
        } catch (ParseException e) {
            logger.error("Failed parsing cli arguments", e);
            System.exit(FAILEDATSETUP);
        }
        return null;
    }

}
