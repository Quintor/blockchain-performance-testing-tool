package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.chart.TimeSeriesChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.TimePeriod;
import net.sf.dynamicreports.report.datasource.DRDataSource;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class TransactionSpeedChartComponentBuilder extends TimeSeriesChartBuilder {

    public TransactionSpeedChartComponentBuilder(Map<Instant, Double> transactionSpeed) {
        TextColumnBuilder<Date> timeColumn = col.column("Time", "time", type.dateYearToSecondType());
        TextColumnBuilder<Double> transactionSpeedColumn = col.column("Transaction Speed", "transactionspeed", type.doubleType());

        setTitle("Average Transaction Speed");
        setTitleFont(stl.font().bold().setFontSize(14));
        setTimePeriod(timeColumn);
        setTimePeriodType(TimePeriod.SECOND);
        series(
                cht.serie(transactionSpeedColumn));
        setValueAxisFormat(cht.axisFormat().setLabel("Transactions / Second"));
        setTimeAxisFormat(cht.axisFormat().setLabel("Time"));
        setDataSource(getDataSource(transactionSpeed));
    }

    private DRDataSource getDataSource(Map<Instant, Double> transactionSpeed){
        DRDataSource dataSource = new DRDataSource("time", "transactionspeed");
        for (Map.Entry<Instant, Double> entry: transactionSpeed.entrySet()) {
            dataSource.add(Date.from(entry.getKey()), entry.getValue());
        }
        return dataSource;
    }
}
