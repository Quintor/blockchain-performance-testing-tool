package nl.quintor.blockchain.ptt.cli.arguments;

import nl.quintor.blockchain.ptt.config.ApplicationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigFileCLIArgumentParserTest {


    private static final String SAMEDIR_INPUT = "fiveTestRunnersOnGanache.yml";
    private static final String PARENTDIR_INPUT = "../fiveTestRunnersOnGanache.yml";
    private static final String ABSOLUTE_INPUT = "/fiveTestRunnersOnGanache.yml";
    private static final String CHILDDIR_INPUT = "folder/fiveTestRunnersOnGanache.yml";
    private static final String INVALIDCHARACTER_INPUT = "?fiveTestRunnersOnGanache.yml";
    @Mock
    private CLIArgumentParser mockArgumentParser;

    @Mock
    private CommandLine commandLine;


    private ConfigFileCLIArgumentParser sut;

    @Test
    public void givenSameDirInputParseCorrectly() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(SAMEDIR_INPUT);
        sut = new ConfigFileCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(Path.of(SAMEDIR_INPUT).toString(), applicationConfig.getFilePath());
    }
    @Test
    public void givenParentDirInputParseCorrectly() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(PARENTDIR_INPUT);
        sut = new ConfigFileCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(Path.of(PARENTDIR_INPUT).toString(), applicationConfig.getFilePath());
    }
    @Test
    public void givenAbsoluteInputParseCorrectly() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(ABSOLUTE_INPUT);
        sut = new ConfigFileCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(Path.of(ABSOLUTE_INPUT).toString(), applicationConfig.getFilePath());
    }
    @Test
    public void givenChildDirParseCorrectly() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(CHILDDIR_INPUT);
        sut = new ConfigFileCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        assertEquals(Path.of(CHILDDIR_INPUT).toString(), applicationConfig.getFilePath());
    }

    @Test
    public void givenNULLInputThenThrowParseException() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(null);
        sut = new ConfigFileCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }

    @Test
    public void givenNoInputThenThrowParseError() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(false);
        sut = new ConfigFileCLIArgumentParser();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        assertThrows(ParseException.class, () -> sut.parse(commandLine, applicationConfig));
    }

    @Test
    public void givenNextParserWithOptionValueThenCallNextParser() throws ParseException {
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(SAMEDIR_INPUT);
        sut = new ConfigFileCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        sut.parse(commandLine, applicationConfig);
        verify(mockArgumentParser).parse(commandLine, applicationConfig);
    }
    @Test
    public void givenNextParserGivesExceptionThenPropagate() throws ParseException {
        sut = new ConfigFileCLIArgumentParser(mockArgumentParser);
        ApplicationConfig applicationConfig = new ApplicationConfig();
        when(commandLine.hasOption("f")).thenReturn(true);
        when(commandLine.getOptionValue("f")).thenReturn(SAMEDIR_INPUT);
        when(mockArgumentParser.parse(commandLine, applicationConfig)).thenThrow(new ParseException("Exception test"));
        assertThrows(ParseException.class, () ->sut.parse(commandLine, applicationConfig));
    }
}