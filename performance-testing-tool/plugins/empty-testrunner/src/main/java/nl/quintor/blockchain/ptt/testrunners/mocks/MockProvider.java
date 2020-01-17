package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionError;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import nl.quintor.blockchain.ptt.testrunners.mocks.ConfirmTransactionMessage;
import nl.quintor.blockchain.ptt.testrunners.mocks.ReceiveTransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class MockProvider extends AbstractActorWithTimers {

    private Logger logger = LoggerFactory.getLogger(MockProvider.class);

    private Duration timeToReceive;
    private Duration timeToConfirm;
    private Double chanceToFail;

    private ActorRef testrunner;


    public MockProvider(Duration timeToReceive, Duration timeToConfirm, Double chanceToFail) {
        this.timeToReceive = timeToReceive;
        this.timeToConfirm = timeToConfirm;
        this.chanceToFail = chanceToFail;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SendTransactionMessage.class, this::sendTransaction)
                .match(ReceiveTransactionMessage.class, this::receiveTransaction)
                .match(ConfirmTransactionMessage.class, this::confirmTransaction)
                .build();
    }

    private void sendTransaction(SendTransactionMessage message) {
        testrunner = getSender();
        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setTimeAtSend(Instant.now());
        getContext().getSystem().getScheduler().scheduleOnce(timeToReceive,
                getSelf(),
                new ReceiveTransactionMessage(transactionResult),
                getContext().getDispatcher(),
                getSelf());

    }

    private boolean takeAChance() {
        Double d = Math.random();
        return chanceToFail < d;
    }

    private void receiveTransaction(ReceiveTransactionMessage message) {
        if (takeAChance()) {
            message.getTransactionResult().setTimeAtReceive(Instant.now());
            getContext().getSystem().getScheduler().scheduleOnce(timeToReceive,
                    getSelf(),
                    new ConfirmTransactionMessage(message.getTransactionResult()),
                    getContext().getDispatcher(),
                    getSelf());
        } else {
            message.getTransactionResult().setTransactionError(new TransactionError("MockP-receive", "receive error", Instant.now()));
            testrunner.tell(message.getTransactionResult(), getSelf());
        }
    }

    private void confirmTransaction(ConfirmTransactionMessage message) {
        if (takeAChance()) {
            message.getTransactionResult().setTimeAtConfirm(Instant.now());
        } else {
            message.getTransactionResult().setTransactionError(new TransactionError("MockP-confirm", "confirm error", Instant.now()));
        }
        testrunner.tell(message.getTransactionResult(), getSelf());
    }
}
