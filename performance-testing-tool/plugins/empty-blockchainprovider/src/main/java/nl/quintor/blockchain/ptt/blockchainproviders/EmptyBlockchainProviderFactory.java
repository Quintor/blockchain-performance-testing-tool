package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.Props;
import nl.quintor.blockchain.ptt.api.IBlockchainProviderFactory;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class EmptyBlockchainProviderFactory implements IBlockchainProviderFactory {

    private Logger logger = LoggerFactory.getLogger(EmptyBlockchainProviderPlugin.class);

    @Override
    public Props createInstance(String blockchainConfigString) throws BlockchainConfigException {

        EmptyBlockchainProviderConfig config = parseStringToConfig(blockchainConfigString);

//        Create the blockchain provider Props object every parameters after EmptyBlockchainProvider.class are given to a constructor of the EmptyBlockchainProvider
        Props blockchainProvider = Props.create(EmptyBlockchainProvider.class, config);

        return blockchainProvider;
    }

//    Parse the string to the config object and ensure all the required values are set
    private EmptyBlockchainProviderConfig parseStringToConfig(String blockchainConfigString) {
        return null;
    }

}
