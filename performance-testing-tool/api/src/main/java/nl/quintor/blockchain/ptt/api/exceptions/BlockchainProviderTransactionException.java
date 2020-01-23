package nl.quintor.blockchain.ptt.api.exceptions;

public class BlockchainProviderTransactionException extends RuntimeException {
    public BlockchainProviderTransactionException(String message) {
        super(message);
    }

    public BlockchainProviderTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
