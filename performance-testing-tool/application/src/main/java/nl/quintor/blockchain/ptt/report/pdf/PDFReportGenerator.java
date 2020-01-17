package nl.quintor.blockchain.ptt.report.pdf;

import akka.actor.AbstractActor;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.MultiPageListBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import nl.quintor.blockchain.ptt.config.TestScenarioConfig;
import nl.quintor.blockchain.ptt.report.messages.PrintReportMessage;
import nl.quintor.blockchain.ptt.report.messages.PrintReportResultMessage;
import nl.quintor.blockchain.ptt.report.pdf.components.ReportComponentLayoutComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;

public class PDFReportGenerator extends AbstractActor {

    private final Logger logger = LoggerFactory.getLogger(getSelf().toString());

    private JasperPdfExporterBuilder pdfExporter;
    private JasperReportBuilder report;
    private TestScenarioConfig config;

    public PDFReportGenerator(String outputFile, TestScenarioConfig config) {
        this.pdfExporter = export.pdfExporter(new File(outputFile + ".pdf"));
        this.report = DynamicReports.report();
        this.config = config;
    }

//    Constructor for testing purposes to mock the report so a pdf isn't generated when running a test
    public PDFReportGenerator(JasperReportBuilder report, String outputFile, TestScenarioConfig config){
        this.pdfExporter = export.pdfExporter(new File(outputFile + ".pdf"));
        this.report = report;
        this.config = config;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PrintReportMessage.class, this::generateReport)
                .build();
    }

    private void generateReport(PrintReportMessage message) {
        if(message.getRawResults() == null || message.getRawResults().isEmpty() || message.getParsedResults() == null){
            logger.warn("No results were given, no report will be generated");
            getSender().tell(new PrintReportResultMessage(false), getSelf());
        }else {

            MultiPageListBuilder reportComponentList = new ReportComponentLayoutComponent(message.getParsedResults(), config);
            try {
                report
                        .title(reportComponentList)
                        .pageFooter(cmp.pageXslashY())
                        .toPdf(pdfExporter);
                logger.info("Succesfully generated report");
                getSender().tell(new PrintReportResultMessage(true), getSelf());
            } catch (DRException e) {
                logger.error("Failed generating report", e);
                getSender().tell(new PrintReportResultMessage(false), getSelf());
            }
        }
    }


}
