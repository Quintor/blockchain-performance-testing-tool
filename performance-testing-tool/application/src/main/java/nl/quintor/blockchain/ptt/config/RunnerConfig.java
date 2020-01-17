package nl.quintor.blockchain.ptt.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class RunnerConfig {

    private String type;
    private String providerId;

    private Map<String, Object> values = new HashMap<>();


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return values;
    }

    @JsonAnySetter
    public void setValues(String name, Object value) {
        values.put(name, value);
    }
}
