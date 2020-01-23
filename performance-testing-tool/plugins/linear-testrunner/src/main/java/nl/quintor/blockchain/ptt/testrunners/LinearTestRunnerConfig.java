package nl.quintor.blockchain.ptt.testrunners;

import nl.quintor.blockchain.ptt.api.exceptions.TestRunnerConfigException;

public class LinearTestRunnerConfig {
    private String name;
    private String type;
    private String providerId;
    private Integer txAmount;
    private String functionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Integer getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(Integer txAmount) {
        this.txAmount = txAmount;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public void validate() throws TestRunnerConfigException {
        if(txAmount == null || txAmount < 1 || functionId == null || functionId.isBlank()){
         throw new TestRunnerConfigException("txAmount must be a positive integer and functionId must be a string");
        }
    }
}
