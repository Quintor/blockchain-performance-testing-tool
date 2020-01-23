package nl.quintor.blockchain.ptt.config;

import java.util.List;

public class SetupProviderConfig {
    List<String> passthroughSetupDataTo;

    public List<String> getPassthroughSetupDataTo() {
        return passthroughSetupDataTo;
    }

    public void setPassthroughSetupDataTo(List<String> passthroughSetupDataTo) {
        this.passthroughSetupDataTo = passthroughSetupDataTo;
    }
}
