package nl.quintor.blockchain.ptt.blockchainproviders;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyperledgerBlockchainProviderPlugin extends Plugin {

    private Logger logger = LoggerFactory.getLogger(HyperledgerBlockchainProviderPlugin.class);

    /**
     * Constructor to be used by plugin manager for plugin instantiation.
     * Your plugins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
     *
     * @param wrapper
     */
    public HyperledgerBlockchainProviderPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("STARTED");
    }

    @Override
    public void stop() {
        logger.info("STOPPED");
    }

    @Override
    public void delete(){
        logger.info("DELETED");
    }
}
