package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.AbstractActor;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//NOT UP TO DATE WITH AKKA ACTOR BLOCKCHAINPROVIDERS
public class FakeBlockchainProvider extends AbstractActor {

    Logger logger = LoggerFactory.getLogger(FakeBlockchainProvider.class);

    private String blockchainConfigFile;

    public FakeBlockchainProvider(String blockchainConfigFile) {
        this.blockchainConfigFile = blockchainConfigFile;
    }

    private void setupNetwork() {
        logger.info("Network Setup Completed");
    }

    private TransactionResult sendTransaction(String functionId) {
        logger.info(functionId + " transaction send");
        TransactionResult result = new TransactionResult();
        logger.info(functionId + " transaction completed");
        return result;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
