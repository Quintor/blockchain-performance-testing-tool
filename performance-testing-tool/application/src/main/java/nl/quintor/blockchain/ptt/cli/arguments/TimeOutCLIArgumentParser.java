package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class TimeOutCLIArgumentParser extends CLIArgumentParser {

    private static final String OPT = "t";
    private static final String LONG_OPT = "timeout";
    private static final boolean HAS_ARG = true;
    private static final String DESCRIPTION = "When set application will timeout after given duration, accepts ISO-8601 duration format";

    public TimeOutCLIArgumentParser() {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION));
    }

    public TimeOutCLIArgumentParser(CLIArgumentParser cliArgumentValidator) {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION), cliArgumentValidator);
    }

    @Override
    public ApplicationConfig parse(CommandLine commandLine, ApplicationConfig applicationConfig) throws ParseException {
        if(commandLine.hasOption("t")) {
            try {
                String durationString = commandLine.getOptionValue(option.getOpt());
                applicationConfig.setTimeout(Duration.parse(durationString));
                if(applicationConfig.getTimeout().isNegative()){
                    throw new ParseException("Invalid argument for timeout, timout can't be negative");
                }
            }catch(NullPointerException | DateTimeParseException e){
                throw new ParseException("Invalid argument for timeout");
            }
        }
        if(nextParser != null){
            return nextParser.parse(commandLine, applicationConfig);
        }
        return applicationConfig;
    }
}
