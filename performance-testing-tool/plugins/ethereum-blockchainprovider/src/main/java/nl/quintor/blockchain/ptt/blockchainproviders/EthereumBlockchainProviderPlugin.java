package nl.quintor.blockchain.ptt.blockchainproviders;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EthereumBlockchainProviderPlugin extends Plugin {

    Logger logger = LoggerFactory.getLogger(EthereumBlockchainProviderPlugin.class);

    /**
     * Constructor to be used by plugin manager for plugin instantiation.
     * Your plugins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
     *
     * @param wrapper
     */
    public EthereumBlockchainProviderPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("STARTED Ethereum blockchainprovider");
    }

    @Override
    public void stop() {
        logger.info("STOPPED Ethereum blockchainprovider");
    }

    @Override
    public void delete(){
        logger.info("DELETED Ethereum blockchainprovider");
    }
}
