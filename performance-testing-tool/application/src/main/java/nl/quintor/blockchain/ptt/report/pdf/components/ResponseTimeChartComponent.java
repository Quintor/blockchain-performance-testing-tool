package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;

import java.util.List;

public class ResponseTimeChartComponent extends HorizontalListBuilder {

    public ResponseTimeChartComponent(List<Long> responseTimesToReceive, List<Long> responseTimesToConfirm) {
        ComponentBuilder<?, ?> responseTimeAtReceiveChart = new SingleBarBoxPlotChartComponent("To Receive", responseTimesToReceive).getChart();
        ComponentBuilder<?, ?> responseTimeAtConfirmChart = new SingleBarBoxPlotChartComponent("To Confirm", responseTimesToConfirm).getChart();
        add(responseTimeAtReceiveChart);
        setGap(25);
        add(responseTimeAtConfirmChart);
    }


}
