package nl.quintor.blockchain.ptt.api.messages;

/**
 * A Message class for communicating between actors
 * <b>Sent by</b> TestRunnerManager
 * <b>Received by</b> a test runner
 * <b>Used for</b> Signaling the test runner to stop sending transactions and shutdown the provider
 * <b>Response</b> Return a TestRunnerResult instance with all the results up to now
 */
public class TestRunnerTerminateMessage{
}
