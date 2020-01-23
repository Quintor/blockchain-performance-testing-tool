package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import nl.quintor.blockchain.ptt.parser.ParsedMetrics;

import java.util.Collections;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class MetricListComponent extends VerticalListBuilder {

    public MetricListComponent(ParsedMetrics parsedMetrics) {
        super();
        add(Components.horizontalList(
                Components.text("Success Rate at Receive (%)").setStyle(stl.style().bold()),
                Components.text("Success Rate at Confirm (%)").setStyle(stl.style().bold()),
                Components.text("Time to Receive (ms)").setStyle(stl.style().bold()),
                Components.text("Time to Confirm (ms)").setStyle(stl.style().bold()),
                Components.text("Average Transactions Per Second").setStyle(stl.style().bold())));

        add(Components.horizontalList(
                Components.text((parsedMetrics.getReceiveSuccesRate() * 100) + "%"),
                Components.text((parsedMetrics.getConfirmSuccesRate() * 100) + "%"),
                Components.text(getMaxResponseTimeField(parsedMetrics.getResponseTimesToReceive(), 1f)),
                Components.text(getMaxResponseTimeField(parsedMetrics.getResponseTimesToConfirm(), 1f)),
                Components.text(parsedMetrics.getTransactionsPerSecond().get(parsedMetrics.getEndTime()))));

        add(Components.horizontalList(
                Components.text(""),
                Components.text(""),
                Components.text(getMaxResponseTimeField(parsedMetrics.getResponseTimesToReceive(), 0.9f)),
                Components.text(getMaxResponseTimeField(parsedMetrics.getResponseTimesToConfirm(), 0.9f)),
                Components.text("")));

        add(Components.horizontalList(
                Components.text(""),
                Components.text(""),
                Components.text(getMaxResponseTimeField(parsedMetrics.getResponseTimesToReceive(), 0.75f)),
                Components.text(getMaxResponseTimeField(parsedMetrics.getResponseTimesToConfirm(), 0.75f)),
                Components.text("")));
    }

    private String getMaxResponseTimeField(List<Long> responseTimes, Float percentile){
        Collections.sort(responseTimes);
        int index = (int) ((responseTimes.size()-1) * percentile);
        return responseTimes.get(index) + "(" + percentile * 100 + "%)";
    }
}

