package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class TimeInformationComponent extends VerticalListBuilder{


    public TimeInformationComponent(Instant beginning, Instant ending) {
        super();
        HorizontalListBuilder startComp = Components.horizontalList();
        HorizontalListBuilder endComp = Components.horizontalList();
        startComp.add(Components.text("Started on").setStyle(stl.style().bold()).setFixedWidth(100), Components.text(Date.from(beginning).toString()));
        endComp.add(Components.text("Ended on").setStyle(stl.style().bold()).setFixedWidth(100), Components.text(Date.from(ending).toString()));

        Duration testDuration = Duration.between(beginning, ending);
        endComp.add(Components.text("Duration").setStyle(stl.style().bold()).setFixedWidth(100), Components.text(String.format("%d:%02d:%02d.%d", testDuration.toHoursPart(), testDuration.toMinutesPart(), testDuration.toSecondsPart(), testDuration.toMillisPart())));
        add(startComp);
        add(endComp);
    }
}
