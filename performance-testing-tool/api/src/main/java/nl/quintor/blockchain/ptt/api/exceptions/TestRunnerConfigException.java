package nl.quintor.blockchain.ptt.api.exceptions;

public class TestRunnerConfigException extends Exception {

    public TestRunnerConfigException(String message) {
        super(message);
    }

    public TestRunnerConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
