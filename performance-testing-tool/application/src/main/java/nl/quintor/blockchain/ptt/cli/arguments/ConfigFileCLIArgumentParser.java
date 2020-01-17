package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigFileCLIArgumentParser extends CLIArgumentParser {

    private static final String OPT = "f";
    private static final String LONG_OPT = "file";
    private static final boolean HAS_ARG = true;
    private static final String DESCRIPTION = "Location of the config file";

    public ConfigFileCLIArgumentParser() {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION));
    }

    public ConfigFileCLIArgumentParser(CLIArgumentParser cliArgumentValidator) {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION), cliArgumentValidator);
    }

    @Override
    public ApplicationConfig parse(CommandLine commandLine, ApplicationConfig applicationConfig) throws ParseException {
        if(commandLine.hasOption(option.getOpt())){
            try {
                Path fileLocationPath = Paths.get(commandLine.getOptionValue(option.getOpt()));
                fileLocationPath.normalize();
                applicationConfig.setFilePath(fileLocationPath.toString());
            }catch(Exception e){
                throw new ParseException("Invalid Argument for file");
            }
        }else{
            throw new ParseException("The file argument is required");
        }
        if(nextParser != null){
           return nextParser.parse(commandLine, applicationConfig);
        }
        return applicationConfig;
    }
}
