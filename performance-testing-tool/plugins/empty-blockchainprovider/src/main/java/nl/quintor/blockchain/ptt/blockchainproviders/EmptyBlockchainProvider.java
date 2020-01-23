package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class EmptyBlockchainProvider extends AbstractActorWithTimers {

    private Logger logger = LoggerFactory.getLogger(EmptyBlockchainProvider.class);

    private EmptyBlockchainProviderConfig blockchainConfig;

    private ActorRef testrunner;


    public EmptyBlockchainProvider(EmptyBlockchainProviderConfig blockchainConfig) {
        this.blockchainConfig = blockchainConfig;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SetupNetworkMessage.class, this::setupNetwork)
                .match(SendTransactionMessage.class, this::sendTransaction)
                .match(ConfirmTransactionMessage.class, this::confirmTransaction)
                .build();
    }

    private void setupNetwork(SetupNetworkMessage message) {
//      Implement setting up the network

//      add setup values relevant for other instances of this provider to the message and return it
        message.setSetupConfigString("variabelen");
        getSender().tell(message, getSelf());
    }


    private void sendTransaction(SendTransactionMessage message) {
        TransactionResult transactionResult = new TransactionResult();
//        Implement sending the transaction with funtionId
        transactionResult.setTimeAtSend(Instant.now());

//        Implement receiving the response and set the time
        transactionResult.setTimeAtReceive(Instant.now());


//        Example for non-blocking transaction confirming with periodic timers
//        Implement confirming the response, this might take a long time and block the actor
//        You can use self sending to periodically poll the network to check confirming only if this class extended from AbstractActorWithTimers
//        This makes it so getSender() doesn't get the testrunner anymore so set the testrunner variable to send the result back to the testrunner
        testrunner = getSender();
        getTimers().startPeriodicTimer(transactionResult.hashCode(), new ConfirmTransactionMessage(transactionResult), Duration.ofSeconds(1));
    }

//    Example method for non-blocking transaction confirming with periodic timers
    private void confirmTransaction(ConfirmTransactionMessage message){
//      Poll the network to confirm the transaction
        message.getTransactionResult().setTimeAtConfirm(Instant.now());

//      Don't forget to Cancel confirm message to avoid reconfirming endlessly
        getTimers().cancel(message.getTransactionResult().hashCode());

//        If the transaction is confirmed or can't be confirmed anymore send the result to the testrunner
        testrunner.tell(message.getTransactionResult(), getSelf());
    }
}
