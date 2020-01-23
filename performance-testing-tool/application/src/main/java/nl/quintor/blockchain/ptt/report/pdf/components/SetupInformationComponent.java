package nl.quintor.blockchain.ptt.report.pdf.components;

import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import nl.quintor.blockchain.ptt.config.TestScenarioConfig;
import nl.quintor.blockchain.ptt.config.ProviderConfig;
import nl.quintor.blockchain.ptt.config.RunnerConfig;

import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class SetupInformationComponent extends VerticalListBuilder {


    public SetupInformationComponent(TestScenarioConfig config) {
        super();
        add(Components.horizontalList(Components.text("Testrunner").setStyle(stl.style().bold()),
                Components.text("Test Type").setStyle(stl.style().bold()),
                Components.text("Provider").setStyle(stl.style().bold()),
                Components.text("Prov Type").setStyle(stl.style().bold())));
        for (Map.Entry<String, RunnerConfig> runnerConfig : config.getRunners().entrySet()) {
            ProviderConfig providerConfig = config.getProviders().get(runnerConfig.getValue().getProviderId());
            HorizontalListBuilder runnerEntree = Components.horizontalList();
            runnerEntree.add(   Components.text(runnerConfig.getKey()),
                                Components.text(runnerConfig.getValue().getType()),
                                Components.text(runnerConfig.getValue().getProviderId()),
                                Components.text(providerConfig.getType()));
            add(runnerEntree);
        }
    }
}
