package nl.quintor.blockchain.ptt.blockchainproviders.config;

import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.util.List;

public class EthereumContract {

    private String binary;
    private String address;
    private BigInteger value = BigInteger.ZERO;
    private List<EthereumFunction> functionList;


    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<EthereumFunction> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<EthereumFunction> functionList) {
        this.functionList = functionList;
    }

    public void validate() throws BlockchainConfigException {
        Boolean invalid = false;
        if(binary == null){
            invalid = true;
        }else if(functionList == null){
            invalid = true;
        }else{
            for (EthereumFunction function : functionList) {
                function.validate();
            }
        }
        if(invalid){
            throw new BlockchainConfigException("Ethereum contract config is invalid");
        }
    }

    public EthereumFunction getConstructor() {
        for (EthereumFunction function : functionList) {
            if (function.getType().equals(FUNCTION_TYPE.CONSTRUCTOR)){
                return function;
            }
        }
        return null;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    public EthereumFunction getFunction(String functionId) {
        for (EthereumFunction function : functionList) {
            if (functionId.equals(function.getName())){
                return function;
            }
        }
        return null;
    }
}
