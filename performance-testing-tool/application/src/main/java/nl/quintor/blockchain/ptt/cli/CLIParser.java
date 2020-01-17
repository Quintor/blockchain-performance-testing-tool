package nl.quintor.blockchain.ptt.cli;

import nl.quintor.blockchain.ptt.cli.arguments.CLIArgumentParser;
import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLIParser {
    private final Logger logger = LoggerFactory.getLogger(CLIParser.class);

    private CommandLine commandLine;
    private HelpFormatter helpFormatter;
    private CLIArgumentParser argumentParser;

    public CLIParser(String[] arguments, CLIArgumentParser argumentParser, CommandLineParser parser, HelpFormatter helpFormatter) throws ParseException {
        this.argumentParser = argumentParser;
        this.helpFormatter = helpFormatter;
        Options options = argumentParser.getOptions(new Options());
        try {
            logger.info("Parsing command line arguments");
            commandLine = parser.parse(options, arguments);
        } catch (ParseException e) {
            this.helpFormatter.printHelp("java -jar ptt.jar [options]", options);
            throw e;
        }
    }


    public ApplicationConfig getApplicationConfig() throws ParseException{
        try {
            return argumentParser.parse(commandLine, new ApplicationConfig());
        }catch(ParseException e){
            helpFormatter.printHelp("java -jar ptt.jar [options]", argumentParser.getOptions(new Options()));
            throw e;
        }
    }
}
