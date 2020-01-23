package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class CLIArgumentParser {
    protected CLIArgumentParser nextParser;
    protected Option option;

    protected CLIArgumentParser(Option option) {
        this.option = option;
    }

    protected CLIArgumentParser(Option option, CLIArgumentParser cliArgumentValidator) {
        this.option = option;
        this.nextParser = cliArgumentValidator;
    }

    public Options getOptions(Options options){
        options.addOption(option);
        if(nextParser !=null){
            nextParser.getOptions(options);
        }
        return options;
    }

    public abstract ApplicationConfig parse(CommandLine commandLine, ApplicationConfig applicationConfig) throws ParseException;

}
