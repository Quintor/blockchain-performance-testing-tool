package nl.quintor.blockchain.ptt.blockchainproviders.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;

@JsonIgnoreProperties({"functions","type"})
public class EthereumBlockchainConfig {
    private String nodeUrl;
    private String wallet;
    private EthereumContract contract;
//    In seconds
    private Integer confirmCheckInterval;

    public Integer getConfirmCheckInterval() {
        return confirmCheckInterval;
    }

    public void setConfirmCheckInterval(Integer confirmCheckInterval) {
        this.confirmCheckInterval = confirmCheckInterval;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public EthereumContract getContract() {
        return contract;
    }

    public void setContract(EthereumContract contract) {
        this.contract = contract;
    }

    @JsonIgnore
    public void validate() throws BlockchainConfigException {
        Boolean invalid = false;
        if(nodeUrl == null || wallet == null || contract == null){
            invalid = true;
        }else{
            contract.validate();
        }

        if(invalid){
            throw new BlockchainConfigException("Config is invalid");
        }
    }
}
