package nl.quintor.blockchain.ptt.report.pdf;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.config.TestScenarioConfig;
import nl.quintor.blockchain.ptt.report.messages.PrintReportMessage;
import nl.quintor.blockchain.ptt.parser.ParsedMetrics;
import nl.quintor.blockchain.ptt.report.messages.PrintReportResultMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PDFReportGeneratorTest {
    private static final String REPORTNAME = "pdfreport";

    private JasperReportBuilder mockReport;

    private ObjectMapper objectMapper;
    private static ActorSystem akkaSystem;
    private ActorRef sut;
    private TestKit testkit;
    private ParsedMetrics parsedMetrics;
    private List<TransactionResult> rawMetrics;
    private TestScenarioConfig testBaseConfig;

    @BeforeAll
    public static void setupSystem() {
        akkaSystem = ActorSystem.create();
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(akkaSystem);
        akkaSystem = null;
    }

    @BeforeEach
    private void setup() throws IOException{
        mockReport = spy(JasperReportBuilder.class);

        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        parsedMetrics = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("parsedTransactionResults.yml"), ParsedMetrics.class);
        rawMetrics = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("rawTransactionResults.yml"), new TypeReference<List<TransactionResult>>(){});
        testBaseConfig = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("fiveTestRunnersOnGanache.yml"), TestScenarioConfig.class);
        testkit = new TestKit(akkaSystem);

        sut = akkaSystem.actorOf(Props.create(PDFReportGenerator.class,mockReport, REPORTNAME, testBaseConfig));
    }

    @Test
    public void givenDefaultParsedMetricsThenSucceed() throws DRException {
        doReturn(mockReport).when(mockReport).toPdf(any(JasperPdfExporterBuilder.class));

        testkit.within(Duration.ofSeconds(10), () ->{
            sut.tell(new PrintReportMessage(parsedMetrics, rawMetrics),testkit.getRef());
            PrintReportResultMessage message = testkit.expectMsgClass(PrintReportResultMessage.class);
            assertTrue(message.isSucces());
            return null;
        });
    }

    @Test
    public void givenErrorDuringPdfGenerationThenFalseSuccessInReturnMessage() throws DRException {
        doThrow(new DRException("Test")).when(mockReport).toPdf(any(JasperPdfExporterBuilder.class));
        testkit.within(Duration.ofSeconds(10), () ->{
            sut.tell(new PrintReportMessage(parsedMetrics, rawMetrics),testkit.getRef());
            PrintReportResultMessage message = testkit.expectMsgClass(PrintReportResultMessage.class);
            assertFalse(message.isSucces());
            return null;
        });
    }
    @Test
    public void givenEmptyRawResultListThenFalseSuccessInReturnMessage() throws DRException {
        testkit.within(Duration.ofSeconds(10), () ->{
            sut.tell(new PrintReportMessage(parsedMetrics, new ArrayList<>()),testkit.getRef());
            PrintReportResultMessage message = testkit.expectMsgClass(PrintReportResultMessage.class);
            assertFalse(message.isSucces());
            return null;
        });
    }
    @Test
    public void givenNullRawResultListThenFalseSuccessInReturnMessage() throws DRException {
        testkit.within(Duration.ofSeconds(10), () ->{
            sut.tell(new PrintReportMessage(parsedMetrics, null),testkit.getRef());
            PrintReportResultMessage message = testkit.expectMsgClass(PrintReportResultMessage.class);
            assertFalse(message.isSucces());
            return null;
        });
    }@Test

    public void givenNullParsedMetricsThenFalseSuccessInReturnMessage() throws DRException {
        testkit.within(Duration.ofSeconds(10), () ->{
            sut.tell(new PrintReportMessage(null, rawMetrics),testkit.getRef());
            PrintReportResultMessage message = testkit.expectMsgClass(PrintReportResultMessage.class);
            assertFalse(message.isSucces());
            return null;
        });
    }
}