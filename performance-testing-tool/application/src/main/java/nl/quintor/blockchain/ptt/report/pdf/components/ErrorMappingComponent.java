package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import nl.quintor.blockchain.ptt.parser.TransactionErrorCount;
import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class ErrorMappingComponent extends VerticalListBuilder {

    public ErrorMappingComponent(Map<String, TransactionErrorCount> errorMap) {
        add(Components.horizontalList(Components.text("Code").setStyle(stl.style().bold()),
                Components.text("Message").setStyle(stl.style().bold()),
                Components.text("Amount").setStyle(stl.style().bold())
                ));
        for (Map.Entry<String, TransactionErrorCount> error : errorMap.entrySet()) {
            HorizontalListBuilder runnerEntree = Components.horizontalList();
            runnerEntree.add(   Components.text(error.getKey()),
                    Components.text(error.getValue().getError().getMessage()),
                    Components.text(error.getValue().getCount())
            );
            add(runnerEntree);
        }
    }
}
