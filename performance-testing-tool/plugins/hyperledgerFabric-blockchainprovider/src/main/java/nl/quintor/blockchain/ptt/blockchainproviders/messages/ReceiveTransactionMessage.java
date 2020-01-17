package nl.quintor.blockchain.ptt.blockchainproviders.messages;

import nl.quintor.blockchain.ptt.api.TransactionResult;

public class ReceiveTransactionMessage {

    private String txId;

    public ReceiveTransactionMessage(String txId) {
        this.txId = txId;
    }

    public String getTxId() {
        return txId;
    }
}
