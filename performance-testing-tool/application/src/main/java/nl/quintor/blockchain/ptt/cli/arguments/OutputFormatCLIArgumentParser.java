package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import nl.quintor.blockchain.ptt.config.OUTPUT_FORMAT;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
public class OutputFormatCLIArgumentParser extends CLIArgumentParser {

    private static final String OPT = "ft";
    private static final String LONG_OPT = "format";
    private static final boolean HAS_ARG = true;
    private static final String DESCRIPTION = "Set format of test report supported formats: pdf,yaml - defaults to pdf";
    private static final OUTPUT_FORMAT DEFAULT_FORMAT = OUTPUT_FORMAT.PDF;

    public OutputFormatCLIArgumentParser() {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION));
    }

    public OutputFormatCLIArgumentParser(CLIArgumentParser cliArgumentValidator) {
        super(new Option(OPT, LONG_OPT, HAS_ARG, DESCRIPTION), cliArgumentValidator);
    }

    @Override
    public ApplicationConfig parse(CommandLine commandLine, ApplicationConfig applicationConfig) throws ParseException {
        if(commandLine.hasOption(option.getOpt()) && commandLine.getOptionValue(option.getOpt()) != null){
            try{
                applicationConfig.setOutputFormat(OUTPUT_FORMAT.valueOf(commandLine.getOptionValue(option.getOpt())));
            }catch(IllegalArgumentException e){
                throw new ParseException("Invalid format");
            }
        }else{
            applicationConfig.setOutputFormat(DEFAULT_FORMAT);
        }
        if(nextParser != null){
            return nextParser.parse(commandLine, applicationConfig);
        }
        return applicationConfig;
    }
}
