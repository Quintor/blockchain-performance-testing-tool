package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.charts.util.DrawChartRendererImpl;
import net.sf.jasperreports.renderers.Renderable;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;

public class SingleBarBoxPlotChartComponent {
    private static final double RANGE_EXTEND_BY_PERCENTAGE = 0.5d;
    private DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
    private String title;
    private Range range;

    public SingleBarBoxPlotChartComponent(String title, List<Long> responseTimes) {
        this.title = title;
        Collections.sort(responseTimes);
        Double mean = calcMean(responseTimes);
        Integer medianIndex = calcMedianIndex(0, responseTimes.size());
        Long median = responseTimes.get(medianIndex);
        Long lowerQuartile = responseTimes.get(calcMedianIndex(0, medianIndex));
        Long upperQuartile = responseTimes.get(calcMedianIndex(medianIndex + 1, responseTimes.size()));
        Long interQuartileRange = upperQuartile - lowerQuartile;
        Double minRegularValue = lowerQuartile - (1.5 * interQuartileRange);
        if (minRegularValue < 0) minRegularValue = 0d;
        Double maxRegularValue = upperQuartile + (1.5 * interQuartileRange);
        List<Double> outliers = new ArrayList<>();
        for (Long time : responseTimes) {
            if (time > maxRegularValue || time < minRegularValue) {
                outliers.add(Double.valueOf(time));
            }
        }
        dataset.add(new BoxAndWhiskerItem(mean, median, lowerQuartile, upperQuartile, minRegularValue, maxRegularValue, minRegularValue, maxRegularValue, outliers), "serie1", "category1");
        double lowerRange = minRegularValue - (((lowerQuartile + interQuartileRange) - minRegularValue) * RANGE_EXTEND_BY_PERCENTAGE);
        double upperRange = maxRegularValue + ((maxRegularValue - (upperQuartile - interQuartileRange)) * RANGE_EXTEND_BY_PERCENTAGE);
        if (lowerRange < 0) lowerRange = 0;
        this.range = new Range(lowerRange, upperRange);
    }

    private Double calcMean(List<Long> responseTimes) {
        Long totalTime = 0L;
        for (Long responseTime : responseTimes) {
            totalTime += responseTime;
        }
        return  (totalTime / (double) responseTimes.size());
    }

    public ImageBuilder getChart() {
        return cmp.image(new ChartExpression()).setFixedDimension(250, 300);
    }

    private Integer calcMedianIndex(int leftIndex, int rightIndex) {
        int n = rightIndex - leftIndex + 1;
        n = (n + 1) / 2 - 1;
        return n + leftIndex;
    }

    private class ChartExpression extends AbstractSimpleExpression<Renderable> {
        private static final long serialVersionUID = 1L;

        @Override
        public Renderable evaluate(ReportParameters reportParameters) {
            CategoryAxis categoryAxis = new CategoryAxis("");
            categoryAxis.setTickLabelsVisible(false);
            NumberAxis valueAxis = new NumberAxis("Time (ms)");
            valueAxis.setRange(range);
            BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
            renderer.setMeanVisible(true);
            renderer.setMaximumBarWidth(0.10);
            CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
            JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.PLAIN, 10), plot, false);
            return new DrawChartRendererImpl(chart, null);
        }
    }
}
