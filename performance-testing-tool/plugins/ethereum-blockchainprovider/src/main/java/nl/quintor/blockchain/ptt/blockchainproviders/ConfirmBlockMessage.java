package nl.quintor.blockchain.ptt.blockchainproviders;

public class ConfirmBlockMessage {
    private String blockHash;

    public ConfirmBlockMessage() {
    }

    public ConfirmBlockMessage(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockHash() {
        return blockHash;
    }
}
