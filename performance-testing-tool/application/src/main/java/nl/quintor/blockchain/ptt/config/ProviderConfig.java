package nl.quintor.blockchain.ptt.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderConfig {

    private String type;
    @JsonIdentityReference
    private List<String> functions;

    private Map<String, Object> values = new HashMap<>();

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
