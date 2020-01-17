package nl.quintor.blockchain.ptt.api.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainProviderTransactionExceptionTest {
    @Test
    public void messageThrowableConstructorTest(){
        assertThrows(BlockchainProviderTransactionException.class, () -> {
            throw new BlockchainProviderTransactionException("Error", new NullPointerException("throwable"));
        });
    }


}