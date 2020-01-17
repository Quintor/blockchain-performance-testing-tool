package nl.quintor.blockchain.ptt.api;

import akka.actor.Props;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import org.pf4j.ExtensionPoint;

public interface IBlockchainProviderFactory extends ExtensionPoint {
    Props createInstance(String blockchainConfig) throws BlockchainConfigException;
}
