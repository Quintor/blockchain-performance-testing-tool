package nl.quintor.blockchain.ptt.api.exceptions;

public class BlockchainConfigException extends Exception {
    public BlockchainConfigException(String message) {
        super(message);
    }

    public BlockchainConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
