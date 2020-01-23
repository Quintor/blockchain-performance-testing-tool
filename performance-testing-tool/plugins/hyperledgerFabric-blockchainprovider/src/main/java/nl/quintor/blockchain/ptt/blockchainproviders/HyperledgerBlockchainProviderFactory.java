package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.quintor.blockchain.ptt.api.IBlockchainProviderFactory;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import org.hyperledger.fabric.gateway.Contract;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Extension
public class HyperledgerBlockchainProviderFactory implements IBlockchainProviderFactory {

    private Logger logger = LoggerFactory.getLogger(HyperledgerBlockchainProviderFactory.class);

    @Override
    public Props createInstance(String blockchainConfigString) throws BlockchainConfigException {

        HyperledgerBlockchainProviderConfig config = parseStringToConfig(blockchainConfigString);

//        Create the blockchain provider Props object every parameters after EmptyBlockchainProvider.class are given to a constructor of the EmptyBlockchainProvider
        Props blockchainProvider = Props.create(HyperledgerBlockchainProvider.class, config);
        return blockchainProvider;
    }

//    Parse the string to the hyperledgerConfig.yml object and ensure all the required values are set
    private HyperledgerBlockchainProviderConfig parseStringToConfig(String blockchainConfigString) throws BlockchainConfigException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            return objectMapper.readValue(blockchainConfigString, HyperledgerBlockchainProviderConfig.class);
        } catch (IOException e) {
            throw new BlockchainConfigException("Failed to parse hyperledgerConfig.yml block", e);
        }
    }

}
