package nl.quintor.blockchain.ptt.blockchainproviders.config;

import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;

import java.util.List;

public class EthereumFunction {

    private FUNCTION_TYPE type;
    private String name;
    private List<Object> inputParameters;
    private List<Object> outputParameters;

    public FUNCTION_TYPE getType() {
        return type;
    }

    public void setType(FUNCTION_TYPE type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<Object> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public List<Object> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(List<Object> outputParameters) {
        this.outputParameters = outputParameters;
    }

    public void validate() throws BlockchainConfigException {
        Boolean invalid = false;
        if(type == null || name == null){
            invalid = true;
        }else if(inputParameters == null || outputParameters == null){
            invalid = true;
        }
        if(invalid){
            throw new BlockchainConfigException("Ethereum function config is invalid");
        }
    }
}
