package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.MultiPageListBuilder;
import nl.quintor.blockchain.ptt.config.TestScenarioConfig;
import nl.quintor.blockchain.ptt.parser.ParsedMetrics;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;

public class ReportComponentLayoutComponent extends MultiPageListBuilder {

    public ReportComponentLayoutComponent(ParsedMetrics parsedMetrics, TestScenarioConfig config) {
        TimeInformationComponent testInformationComponent = new TimeInformationComponent(parsedMetrics.getBeginTime(), parsedMetrics.getEndTime());
        SetupInformationComponent testSetupInformationComponent = new SetupInformationComponent(config);
        MetricListComponent metricListComponent = new MetricListComponent(parsedMetrics);
        TransactionSpeedChartComponentBuilder transactionSpeedChartComponentBuilder = new TransactionSpeedChartComponentBuilder(parsedMetrics.getTransactionsPerSecond());
        StatusOverviewTimelineChartComponent statusOverviewTimelineChartComponent = new StatusOverviewTimelineChartComponent(parsedMetrics.getStatusTimeline());
        ResponseTimeChartComponent responseTimeChartComponent = new ResponseTimeChartComponent(parsedMetrics.getResponseTimesToReceive(), parsedMetrics.getResponseTimesToConfirm());
        ErrorMappingComponent errorMappingComponent = new ErrorMappingComponent(parsedMetrics.getErrorMap());

        add(
                new TitleTextComponent("Test Report"),
                testInformationComponent,
                new HeaderTextComponent("Metrics"),
                metricListComponent,
                cmp.filler().setFixedHeight(20),
                new SmallHeaderTextComponent("Response Times"),
                cmp.filler().setFixedHeight(10),
                cmp.centerHorizontal(responseTimeChartComponent),
                cmp.filler().setFixedHeight(20),
                statusOverviewTimelineChartComponent,
                transactionSpeedChartComponentBuilder,
                cmp.filler().setFixedHeight(20),
                new SmallHeaderTextComponent("Errors"),
                cmp.filler().setFixedHeight(10),
                errorMappingComponent,
                cmp.filler().setFixedHeight(20),
                new SmallHeaderTextComponent("Test Setup"),
                cmp.filler().setFixedHeight(10),
                testSetupInformationComponent);
    }
}
