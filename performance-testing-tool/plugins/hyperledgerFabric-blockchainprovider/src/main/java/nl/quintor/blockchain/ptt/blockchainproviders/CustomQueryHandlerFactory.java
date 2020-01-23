package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.ActorRef;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.spi.QueryHandler;
import org.hyperledger.fabric.gateway.spi.QueryHandlerFactory;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.util.Collection;

public class CustomQueryHandlerFactory implements QueryHandlerFactory {
    private ActorRef actorRef;

    public CustomQueryHandlerFactory(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    @Override
    public QueryHandler create(Network network) {
        return new CustomQueryHandler(getPeersForOrganization(network), actorRef);
    }

    private static Collection<Peer> getPeersForOrganization(final Network network) {
        String mspId = network.getGateway().getIdentity().getMspId();
        try {
            return network.getChannel().getPeersForOrganization(mspId);
        } catch (InvalidArgumentException e) {
            // This should never happen as mspId should not be null
            throw new RuntimeException(e);
        }
    }
}
