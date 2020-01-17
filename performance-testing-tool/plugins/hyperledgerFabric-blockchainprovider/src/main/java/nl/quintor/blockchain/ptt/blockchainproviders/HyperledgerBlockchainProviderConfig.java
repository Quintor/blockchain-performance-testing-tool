package nl.quintor.blockchain.ptt.blockchainproviders;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;

import java.util.Map;

public class HyperledgerBlockchainProviderConfig {

    private String networkName;
    private String contractName;

    private String networkConfigFile;

    private String username;
    private String orgMsp;
    private String userCertificate;
    private String userPrivateKey;

    private Map<String, HyperledgerFunction> functionList;

    private Integer maxAttempts;

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getContractName() {
        return contractName;
    }

    public String getOrgMsp() {
        return orgMsp;
    }

    public void setOrgMsp(String orgMsp) {
        this.orgMsp = orgMsp;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getNetworkConfigFile() {
        return networkConfigFile;
    }

    public void setNetworkConfigFile(String networkConfigFile) {
        this.networkConfigFile = networkConfigFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserCertificate() {
        return userCertificate;
    }

    public void setUserCertificate(String userCertificate) {
        this.userCertificate = userCertificate;
    }

    public String getUserPrivateKey() {
        return userPrivateKey;
    }

    public void setUserPrivateKey(String userPrivateKey) {
        this.userPrivateKey = userPrivateKey;
    }

    public Map<String, HyperledgerFunction> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(Map<String, HyperledgerFunction> functionList) {
        this.functionList = functionList;
    }
}
