package nl.quintor.blockchain.ptt.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

public class TransactionError {
    private String code;
    private String message;
    private Instant time;

    @JsonCreator
    public TransactionError(@JsonProperty("code") String code, @JsonProperty("message")String message, @JsonProperty("time")Instant time) {
        this.code = code;
        this.message = message;
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public Instant getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

//    TransactionErrors are identified by the code, thus an TransactionError is the same if the code is the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionError that = (TransactionError) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
