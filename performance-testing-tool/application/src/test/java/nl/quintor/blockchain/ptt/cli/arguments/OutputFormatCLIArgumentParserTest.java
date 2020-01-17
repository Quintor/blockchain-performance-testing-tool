package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import nl.quintor.blockchain.ptt.config.OUTPUT_FORMAT;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutputFormatCLIArgumentParserTest {


    private static final String DEFAULT_FORMAT = OUTPUT_FORMAT.PDF.toString();
    private static final String NOTDEFAULT_INPUT = OUTPUT_FORMAT.YAML.toString();

    @Mock
    private CLIArgumentParser mockArgumentParser;

    @Mock
    private CommandLine commandLine;


    private OutputFormatCLIArgumentParser sut;

    @Test
    public void givenConstructParseCorrectly() throws ParseException {
        when(commandLine.hasOption("ft")).thenReturn(true);
        when(commandLine.getOptionValue("ft")).thenReturn(DEFAULT_FORMAT);
        sut = new OutputFormatCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(OUTPUT_FORMAT.valueOf(DEFAULT_FORMAT), applicationConfig.getOutputFormat());
    }

    @Test
    public void givenNULLInputThenThrowParseException() throws ParseException {
        when(commandLine.hasOption("ft")).thenReturn(true);
        when(commandLine.getOptionValue("ft")).thenReturn(null);
        sut = new OutputFormatCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(OUTPUT_FORMAT.valueOf(DEFAULT_FORMAT), applicationConfig.getOutputFormat());
    }

    @Test
    public void givenNoInputThenSetDefault() throws ParseException {
        when(commandLine.hasOption("ft")).thenReturn(false);
        sut = new OutputFormatCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(OUTPUT_FORMAT.PDF, applicationConfig.getOutputFormat());
    }

    @Test
    public void givenNextParserButNoOptionValueThenCallNextParser() throws ParseException {
        when(commandLine.hasOption("ft")).thenReturn(false);
        sut = new OutputFormatCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        verify(mockArgumentParser).parse(commandLine, applicationConfig);
    }

    @Test
    public void givenNextParserWithOptionValueThenCallNextParser() throws ParseException {
        when(commandLine.hasOption("ft")).thenReturn(true);
        when(commandLine.getOptionValue("ft")).thenReturn(DEFAULT_FORMAT);
        sut = new OutputFormatCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        verify(mockArgumentParser).parse(commandLine, applicationConfig);
    }
    @Test
    public void givenNextParserGivesExceptionThenPropagate() throws ParseException {
        sut = new OutputFormatCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        when(commandLine.hasOption("ft")).thenReturn(true);
        when(commandLine.getOptionValue("ft")).thenReturn(DEFAULT_FORMAT);
        when(mockArgumentParser.parse(commandLine, applicationConfig)).thenThrow(new ParseException("Exception test"));
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }
}