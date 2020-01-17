package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.Props;
import nl.quintor.blockchain.ptt.api.IBlockchainProviderFactory;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class FakeBlockchainProviderFactory implements IBlockchainProviderFactory {

    Logger logger = LoggerFactory.getLogger(FakeBlockchainProviderFactory.class);

    @Override
    public Props createInstance(String blockchainConfig) {
        logger.info("Creating new instance of FakeBlockchainProvider");
        return Props.create(FakeBlockchainProvider.class, blockchainConfig);
    }
}
