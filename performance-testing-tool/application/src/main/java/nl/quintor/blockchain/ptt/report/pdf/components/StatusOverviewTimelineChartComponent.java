package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.chart.TimeSeriesChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.TimePeriod;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import nl.quintor.blockchain.ptt.parser.StatusOverviewTimelineSnapshot;
import org.apache.commons.collections.map.HashedMap;

import java.awt.*;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class StatusOverviewTimelineChartComponent extends TimeSeriesChartBuilder {


    public StatusOverviewTimelineChartComponent(Map<Instant, StatusOverviewTimelineSnapshot> statusTimeline) {
        TextColumnBuilder<Date> timeColumn = col.column("Time", "time", type.dateYearToSecondType());
        TextColumnBuilder<Integer> sendColumn = col.column("Send", "amountSend", type.integerType());
        TextColumnBuilder<Integer> receivedColumn = col.column("Received", "amountReceived", type.integerType());
        TextColumnBuilder<Integer> confirmedColumn = col.column("Confirmed", "amountConfirmed", type.integerType());
        TextColumnBuilder<Integer> failedColumn = col.column("Failed", "amountFailed", type.integerType());

        HashedMap seriesColors = new HashedMap();
        seriesColors.put("Send", Color.BLUE);
        seriesColors.put("Received", Color.ORANGE);
        seriesColors.put("Confirmed", Color.GREEN);
        seriesColors.put("Failed", Color.RED);

        setTitle("Transaction Status Timeline");
        setTitleFont(stl.font().bold().setFontSize(14));
        setTimePeriod(timeColumn);
        setTimePeriodType(TimePeriod.SECOND);
        series(
                cht.serie(sendColumn),
                cht.serie(receivedColumn),
                cht.serie(confirmedColumn),
                cht.serie(failedColumn)).setShowShapes(false);
        setValueAxisFormat(cht.axisFormat().setLabel("Transaction Amount"));
        setTimeAxisFormat(cht.axisFormat().setLabel("Time"));
        seriesColorsByName(seriesColors);
        setDataSource(getDataSource(statusTimeline));
    }

    private DRDataSource getDataSource(Map<Instant, StatusOverviewTimelineSnapshot> statusTimeline) {
        DRDataSource dataSource = new DRDataSource("time", "amountSend", "amountReceived", "amountConfirmed", "amountFailed");
        Date previousDate = null;
        StatusOverviewTimelineSnapshot snapshot = null;
        Boolean newDate = true;
        for (Map.Entry<Instant, StatusOverviewTimelineSnapshot> entry : statusTimeline.entrySet()) {
            if (newDate) {
                previousDate = Date.from(entry.getKey());
                snapshot = entry.getValue();
                newDate = false;
            }else{
                if(!previousDate.toString().equals(Date.from(entry.getKey()).toString())) {
                    Integer amountSend = snapshot.getAmountSend();
                    Integer amountReceived = snapshot.getAmountReceived();
                    Integer amountConfirmed = snapshot.getAmountConfirmed();
                    Integer amountFailed = snapshot.getAmountFailed();
                    dataSource.add(previousDate, amountSend, amountReceived, amountConfirmed, amountFailed);
                    previousDate = Date.from(entry.getKey());
                }
                snapshot = entry.getValue();
            }
        }
        if(snapshot!= null) {
            Integer amountSend = snapshot.getAmountSend();
            Integer amountReceived = snapshot.getAmountReceived();
            Integer amountConfirmed = snapshot.getAmountConfirmed();
            Integer amountFailed = snapshot.getAmountFailed();
            dataSource.add(previousDate, amountSend, amountReceived, amountConfirmed, amountFailed);
        }
        return dataSource;
    }
}
