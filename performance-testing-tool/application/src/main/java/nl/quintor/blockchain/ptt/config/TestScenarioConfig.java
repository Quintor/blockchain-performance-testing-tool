package nl.quintor.blockchain.ptt.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TestScenarioConfig {
    @JsonProperty
    private Map<String, RunnerConfig> runners;
    @JsonProperty
    private Map<String, ProviderConfig> providers;
    @JsonProperty
    private Map<String, SetupProviderConfig> useProvidersForSetup;

    public Map<String, SetupProviderConfig> getUseProvidersForSetup() {
        return useProvidersForSetup;
    }

    public void setUseProvidersForSetup(Map<String, SetupProviderConfig> useProvidersForSetup) {
        this.useProvidersForSetup = useProvidersForSetup;
    }

    public Map<String, RunnerConfig> getRunners() {
        return runners;
    }

    public void setRunners(Map<String, RunnerConfig> runners) {
        this.runners = runners;
    }

    public Map<String, ProviderConfig> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, ProviderConfig> providers) {
        this.providers = providers;
    }
}
