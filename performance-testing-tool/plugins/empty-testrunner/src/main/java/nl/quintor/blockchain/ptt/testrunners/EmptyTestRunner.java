package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerResultMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerStartMessage;
import nl.quintor.blockchain.ptt.api.messages.TestRunnerTerminateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EmptyTestRunner extends AbstractActor {

    private Logger logger = LoggerFactory.getLogger(getSelf().toString());

    private EmptyTestRunnerConfig config;

    private List<TransactionResult> results;

    private ActorRef provider;
    private ActorRef manager;


    public EmptyTestRunner(EmptyTestRunnerConfig config, ActorRef provider) {
        results = new ArrayList<>();
        this.config = config;
        this.provider = provider;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestRunnerStartMessage.class, this::start)
                .match(TransactionResult.class, this::addResult)
                .match(TestRunnerTerminateMessage.class, this::terminate)
                .build();
    }

//      Start sending transactions
    private void start(TestRunnerStartMessage message) {
//      Set manager reference to send all TransactionResults to the manager when finished
        manager = getSender();

//      IMPLEMENT sending transactions remember to be non blocking to not block a timeout message.
        provider.tell(new SendTransactionMessage(""), getSelf());
    }

//      Add the Transaction result to the list
    private void addResult(TransactionResult result) {
        results.add(result);

//      IMPLEMENT check if all transactions are send and then send TestRunnerResultMessage back to the manager

        manager.tell(new TestRunnerResultMessage(results), getSelf());
    }

//    Stop sending transactions and return all the results
    private void terminate(TestRunnerTerminateMessage message) {
//      IMPLEMENT stop sending transactions

//      Terminate the provider because it might be self messaging to check for transaction state changes
        getContext().getSystem().stop(provider);

        manager.tell(new TestRunnerResultMessage(results), getSelf());
    }

}
