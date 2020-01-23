package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.quintor.blockchain.ptt.api.IBlockchainProviderFactory;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumBlockchainConfig;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Extension
public class EthereumBlockchainProviderFactory implements IBlockchainProviderFactory {

    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(EthereumBlockchainProviderFactory.class);

    @Override
    public Props createInstance(String blockchainConfigString) throws BlockchainConfigException {
        if(objectMapper == null){
            objectMapper = new ObjectMapper(new YAMLFactory());
        }
        if(blockchainConfigString == null){
            throw new BlockchainConfigException("Config string can't be null");
        }
        EthereumBlockchainConfig blockchainConfig;
        try{
            blockchainConfig = objectMapper.readValue(blockchainConfigString, EthereumBlockchainConfig.class);
            blockchainConfig.validate();
            if(blockchainConfig.getConfirmCheckInterval() == null){
                logger.warn("ConfirmCheckInterval is not set the blockchain network might not be able to handle the load of continous polling of all the transactions and crash.");
            }
        }catch(IOException e){
            throw new BlockchainConfigException("Couldn't parse blockchain config", e);
        }
        Props provider = Props.create(EthereumBlockchainProvider.class, blockchainConfig);
        return provider;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
