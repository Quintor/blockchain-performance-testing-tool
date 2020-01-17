package nl.quintor.blockchain.ptt.blockchainproviders;

import org.hyperledger.fabric.gateway.ContractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TransactionStateListener implements Consumer<ContractEvent> {

    private Logger logger = LoggerFactory.getLogger(TransactionStateListener.class);

    @Override
    public void accept(ContractEvent contractEvent) {
        logger.info(contractEvent.getTransactionEvent().toString());
    }
}
