package nl.quintor.blockchain.ptt.api.messages;

import nl.quintor.blockchain.ptt.api.TransactionResult;

import java.util.List;

/**
 * A Message class for communicating between actors
 * <b>Sent by</b> a test runner
 * <b>Received by</b> TestRunnerManager
 * <b>Used for</b> Returning all the TransactionResults the test runners collected from the blockchain provider after sending all the transactions
 */
public class TestRunnerResultMessage{
    private List<TransactionResult> resultList;

    public TestRunnerResultMessage(List<TransactionResult> resultList) {
        this.resultList = resultList;
    }

    public TestRunnerResultMessage() {

    }

    public List<TransactionResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<TransactionResult> resultList) {
        this.resultList = resultList;
    }
}
