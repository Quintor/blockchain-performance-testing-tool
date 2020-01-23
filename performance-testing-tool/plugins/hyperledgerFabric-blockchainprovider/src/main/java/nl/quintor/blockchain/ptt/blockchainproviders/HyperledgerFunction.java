package nl.quintor.blockchain.ptt.blockchainproviders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HyperledgerFunction {
    private String type;
    private String name;
    private String[] arguments;

    @JsonCreator
    public HyperledgerFunction(@JsonProperty("type") String type, @JsonProperty("name") String name, @JsonProperty("arguments")String... arguments) {
        this.type = type;
        this.name = name;
        this.arguments = arguments;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments;
    }
}
