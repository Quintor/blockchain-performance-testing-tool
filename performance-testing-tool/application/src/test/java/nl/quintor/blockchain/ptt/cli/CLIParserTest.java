package nl.quintor.blockchain.ptt.cli;

import nl.quintor.blockchain.ptt.cli.arguments.CLIArgumentParser;
import nl.quintor.blockchain.ptt.cli.arguments.ConfigFileCLIArgumentParser;
import nl.quintor.blockchain.ptt.cli.arguments.OutputFileCLIArgumentParser;
import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CLIParserTest {

    @Mock
    private DefaultParser defaultParser;
    @Mock
    private HelpFormatter helpFormatter;
    @Mock
    private CommandLine commandLine;
    @Mock
    private CLIArgumentParser argumentParser1;
    @Mock
    private Option option1;

    private CLIParser sut;


    @Test
    public void given2ValidatorsThenGetOptionsWith2Entrees() throws ParseException {
        String[] arguments = new String[]{};
        Options options = new Options().addOption(option1);
        when(argumentParser1.getOptions(any(Options.class))).thenReturn(options);
        sut = new CLIParser(arguments, argumentParser1, defaultParser, helpFormatter);
        verify(defaultParser).parse(options, arguments);
    }

    @Test
    public void givenInvalidArgumentsThenHelpIsPrinted() throws ParseException {
        String[] arguments = new String[]{};
        Options options = new Options().addOption(option1);
        when(argumentParser1.getOptions(any(Options.class))).thenReturn(options);
        when(defaultParser.parse(options, arguments)).thenThrow(new ParseException("ParseExceptionTest"));
        assertThrows(ParseException.class, () -> {
            sut = new CLIParser(arguments, argumentParser1, defaultParser, helpFormatter);
                });
        verify(helpFormatter).printHelp(anyString(), eq(options));
    }
    @Test
    public void givenGetApplicationConfigThenParserIsCalledForAParse() throws ParseException {
        String[] arguments = new String[]{};
        Options options = new Options().addOption(option1);
        when(argumentParser1.getOptions(any(Options.class))).thenReturn(options);
        when(defaultParser.parse(options, arguments)).thenReturn(commandLine);
        when(argumentParser1.parse(any(CommandLine.class), any(ApplicationConfig.class))).thenReturn(new ApplicationConfig());
        sut = new CLIParser(arguments, argumentParser1, defaultParser, helpFormatter);
        sut.getApplicationConfig();
        verify(argumentParser1).parse(any(CommandLine.class), any(ApplicationConfig.class));
    }

    @Test
    public void givenConfigFileParserThenApplicationConfigisGivenWithFilePath() throws ParseException {
        String[] arguments = new String[]{"-f", "test.yml"};
        sut = new CLIParser(arguments, new ConfigFileCLIArgumentParser(new OutputFileCLIArgumentParser()), new DefaultParser(), new HelpFormatter());
        assertNotNull(sut.getApplicationConfig());
    }
}