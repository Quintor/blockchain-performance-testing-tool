package nl.quintor.blockchain.ptt.parser;

import nl.quintor.blockchain.ptt.api.TransactionResult;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents an status change (event) of a transaction in the provider.
 * Example - transaction is send by the provider, transaction is confirmed on the blockchain by the provider
 */
public class ProviderEvent implements Comparable<ProviderEvent> {
    private Instant time;
    private EventType type;
    private TransactionResult result;

    public ProviderEvent(Instant time, EventType type, TransactionResult result) {
        this.time = time;
        this.type = type;
        this.result = result;
    }

    public TransactionResult getResult() {
        return result;
    }


    public Instant getTime() {
        return time;
    }


    public EventType getType() {
        return type;
    }


    @Override
    public int compareTo(ProviderEvent providerEvent) {
        return this.time.compareTo(providerEvent.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderEvent that = (ProviderEvent) o;
        return time.equals(that.time) &&
                type == that.type &&
                result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, type, result);
    }
}
