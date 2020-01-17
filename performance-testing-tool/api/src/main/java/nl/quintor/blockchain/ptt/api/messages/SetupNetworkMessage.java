package nl.quintor.blockchain.ptt.api.messages;

/**
 * A Message class for communicating between actors
 * <b>Sent by</b> the application
 * <b>Received by</b> a Blockchain Provider
 * <b>Used for</b> Signaling the provider to execute it's setup functionality or use the setupConfigString variables to setup itself
 * <b>Response</b> SetupNetworkMessage instance with setupConfigString set
 */
public class SetupNetworkMessage {
    /**
     * An encoded string which the blockchain provider can decode into setup variables
     */
    private String setupConfigString;

    public SetupNetworkMessage() {
    }

    public SetupNetworkMessage(String setupConfigString) {
        this.setupConfigString = setupConfigString;
    }

    public String getSetupConfigString() {
        return setupConfigString;
    }

    public void setSetupConfigString(String setupConfigString) {
        this.setupConfigString = setupConfigString;
    }
}
