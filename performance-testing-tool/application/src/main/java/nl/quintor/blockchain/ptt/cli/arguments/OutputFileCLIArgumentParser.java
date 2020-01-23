package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputFileCLIArgumentParser extends CLIArgumentParser {

    private static final String OPT = "o";
    private static final String LONG_OPT = "output";
    private static final boolean HAS_ARG = true;
    private static final String DESCRIPTION = "Set if you want to change the filename and/or path of the test report";

    public OutputFileCLIArgumentParser() {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION));
    }

    public OutputFileCLIArgumentParser( CLIArgumentParser cliArgumentValidator) {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION), cliArgumentValidator);
    }

    @Override
    public ApplicationConfig parse(CommandLine commandLine, ApplicationConfig applicationConfig) throws ParseException {
        if(commandLine.hasOption(option.getOpt())){
            try{
                Path outputFile = Paths.get(commandLine.getOptionValue(option.getOpt()));
                outputFile.normalize();
                applicationConfig.setOutput(outputFile.toString());
            }catch(Exception e){
                throw new ParseException("Invalid argument for output");
            }
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd-HHmm");
            applicationConfig.setOutput("ptt-report-" + simpleDateFormat.format(new Date()));
        }
        if(nextParser != null){
            return nextParser.parse(commandLine, applicationConfig);
        }
        return applicationConfig;
    }
}
