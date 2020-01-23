package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.ActorRef;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.impl.AllCommitStrategy;
import org.hyperledger.fabric.gateway.impl.CommitStrategy;
import org.hyperledger.fabric.gateway.spi.CommitHandler;
import org.hyperledger.fabric.gateway.spi.CommitHandlerFactory;
import org.hyperledger.fabric.gateway.spi.QueryHandler;
import org.hyperledger.fabric.sdk.Peer;

import java.util.Collection;
import java.util.EnumSet;

public class CustomCommitHandlerFactory implements CommitHandlerFactory {

    private ActorRef actorRef;

    public CustomCommitHandlerFactory(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    @Override
    public CommitHandler create(String transactionId, Network network) {
        Collection<Peer> peers = getEventSourcePeers(network);
        CommitStrategy strategy = new AllCommitStrategy(peers);
        return new CustomCommitHandler(transactionId, network, strategy, actorRef);
    }

    private static Collection<Peer> getEventSourcePeers(final Network network) {
        return network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE));
    }
}
