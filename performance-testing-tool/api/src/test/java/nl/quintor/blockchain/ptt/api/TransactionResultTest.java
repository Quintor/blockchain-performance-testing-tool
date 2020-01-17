package nl.quintor.blockchain.ptt.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionResultTest {

    private TransactionResult sut;


    @BeforeEach
    public void setup(){
        sut = new TransactionResult();
    }


    @Test
    public void givenAllTimesIsValidIsTrue(){
        sut.setTimeAtSend(Instant.now());
        sut.setTimeAtReceive(Instant.now());
        sut.setTimeAtConfirm(Instant.now());
        assertTrue(sut.isInvalidEndState());
    }
    @Test
    public void givenNoConfirmTimeWithErrorIsValidIsTrue(){
        sut.setTimeAtSend(Instant.now());
        sut.setTimeAtReceive(Instant.now());
        sut.setTransactionError(new TransactionError("testcode","testmessage", Instant.now()));
        assertTrue(sut.isInvalidEndState());
    }
    @Test
    public void givenNoReceiveTimeAndNoConfirmTimeWithErrorIsValidIsTrue(){
        sut.setTimeAtSend(Instant.now());
        sut.setTransactionError(new TransactionError("testcode","testmessage", Instant.now()));
        assertTrue(sut.isInvalidEndState());
    }

    @Test
    public void givenNoReceiveTimeWithConfirmTimeWithErrorIsValidIsFalse(){
        sut.setTimeAtSend(Instant.now());
        sut.setTimeAtConfirm(Instant.now());
        sut.setTransactionError(new TransactionError("testcode","testmessage", Instant.now()));
        assertFalse(sut.isInvalidEndState());
    }

    @Test
    public void givenNoReceiveTimeWithConfirmTimeWithoutErrorIsValidIsFalse(){
        sut.setTimeAtSend(Instant.now());
        sut.setTimeAtConfirm(Instant.now());
        assertFalse(sut.isInvalidEndState());
    }

    @Test
    public void givenNoSendTimeIsValidIsFalse(){
        assertFalse(sut.isInvalidEndState());
    }
    @Test
    public void givenNoSendTimeTimeWithErrorIsValidIsfalse(){
        sut.setTimeAtReceive(Instant.now());
        sut.setTransactionError(new TransactionError("testcode","testmessage", Instant.now()));
        assertFalse(sut.isInvalidEndState());
    }
    @Test
    public void givenNoSendTimeTimeAndNoConfirmTimeWithErrorIsValidIsFalse(){
        sut.setTransactionError(new TransactionError("testcode","testmessage", Instant.now()));
        assertFalse(sut.isInvalidEndState());
    }

    @Test
    public void givenNoSendTimeTimeWithConfirmTimeWithErrorIsValidIsFalse(){
        sut.setTimeAtConfirm(Instant.now());
        sut.setTransactionError(new TransactionError("testcode","testmessage", Instant.now()));
        assertFalse(sut.isInvalidEndState());
    }

    @Test
    public void givenNoSendTimeNoReceiveTimeWithConfirmTimeWithoutErrorIsValidIsFalse(){
        sut.setTimeAtConfirm(Instant.now());
        assertFalse(sut.isInvalidEndState());
    }

}