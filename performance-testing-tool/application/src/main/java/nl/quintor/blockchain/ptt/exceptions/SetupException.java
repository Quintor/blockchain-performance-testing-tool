package nl.quintor.blockchain.ptt.exceptions;

public class SetupException extends Exception {

    public SetupException(String message) {
        super(message);
    }

    public SetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
