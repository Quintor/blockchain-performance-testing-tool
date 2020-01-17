package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeOutCLIArgumentParserTest {

    private static final String VALID_INPUT = "PT20S";
    private static final String NOTISO_INPUT = "20";
    private static final String NEGATIVE_INPUT = "PT-6H";
    @Mock
    private CLIArgumentParser mockArgumentParser;

    @Mock
    private CommandLine commandLine;


    private TimeOutCLIArgumentParser sut;

    @Test
    public void givenConstructParseCorrectly() throws ParseException {
        when(commandLine.hasOption("t")).thenReturn(true);
        when(commandLine.getOptionValue("t")).thenReturn(VALID_INPUT);
        sut = new TimeOutCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(Duration.parse(VALID_INPUT), applicationConfig.getTimeout());
    }

    @Test
    public void givenNotISOInputThenThrowParseException() throws ParseException {
        when(commandLine.hasOption("t")).thenReturn(true);
        when(commandLine.getOptionValue("t")).thenReturn(NOTISO_INPUT);
        sut = new TimeOutCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }

    @Test
    public void givenNULLInputThenThrowParseException() throws ParseException {
        when(commandLine.hasOption("t")).thenReturn(true);
        when(commandLine.getOptionValue("t")).thenReturn(null);
        sut = new TimeOutCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }

    @Test
    public void givenNoInputThenDontSetInConfig() throws ParseException {
        when(commandLine.hasOption("t")).thenReturn(false);
        sut = new TimeOutCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(null, applicationConfig.getTimeout());
    }

    @Test
    public void givenNextParserButNoOptionValueThenCallNextParser() throws ParseException {
        when(commandLine.hasOption("t")).thenReturn(false);
        sut = new TimeOutCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        verify(mockArgumentParser).parse(commandLine, applicationConfig);
    }

    @Test
    public void givenNextParserWithOptionValueThenCallNextParser() throws ParseException {
        when(commandLine.hasOption("t")).thenReturn(true);
        when(commandLine.getOptionValue("t")).thenReturn(VALID_INPUT);
        sut = new TimeOutCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        verify(mockArgumentParser).parse(commandLine, applicationConfig);
    }
    @Test
    public void givenNextParserGivesExceptionThenPropagate() throws ParseException {
        sut = new TimeOutCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        when(commandLine.hasOption("t")).thenReturn(true);
        when(commandLine.getOptionValue("t")).thenReturn(VALID_INPUT);
        when(mockArgumentParser.parse(commandLine, applicationConfig)).thenThrow(new ParseException("Exception test"));
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }
    
    @Test
    public void givenNegativeValueThenThrowException(){
        sut = new TimeOutCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        when(commandLine.hasOption("t")).thenReturn(true);
        when(commandLine.getOptionValue("t")).thenReturn(NEGATIVE_INPUT);
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }
}