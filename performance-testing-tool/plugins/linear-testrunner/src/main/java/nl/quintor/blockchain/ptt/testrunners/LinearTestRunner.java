package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LinearTestRunner extends AbstractActor {

    private Logger logger = LoggerFactory.getLogger(LinearTestRunner.class);

    private LinearTestRunnerConfig config;

    private List<TransactionResult> results;

    private ActorRef provider;
    private ActorRef manager;

    private Integer transactionsRunning;

    public LinearTestRunner(LinearTestRunnerConfig config, ActorRef provider) {
        this.config = config;
        this.provider = provider;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestRunnerStartMessage.class, this::start)
                .match(TestRunnerResultMessage.class, this::stop)
                .match(TransactionResult.class, this::addResult)
                .match(TestRunnerTerminateMessage.class, this::terminate)
                .build();
    }

    private void terminate(TestRunnerTerminateMessage message) {
        getContext().getSystem().stop(provider);
        transactionsRunning = -1;
        manager.tell(new TestRunnerResultMessage(results), getSelf());
    }

    private void addResult(TransactionResult result) {
        result.setRunner(config.getName());
        results.add(result);
        transactionsRunning--;
        if(transactionsRunning == 0){
            manager.tell(new TestRunnerResultMessage(results), getSelf());
        }
    }

    private  void stop(TestRunnerResultMessage message) {
        logger.info("Received {}", message.getClass().toString());
    }

    private void start(TestRunnerStartMessage message) {
        logger.info("Starting to run");
        this.transactionsRunning = config.getTxAmount();
        this.manager = getSender();
        this.results = new ArrayList<>();
        for (int i = 0; i < config.getTxAmount(); i++) {
            provider.tell(new SendTransactionMessage(config.getFunctionId()), getSelf());
        }
    }

}
