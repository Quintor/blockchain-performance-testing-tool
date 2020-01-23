package nl.quintor.blockchain.ptt.api.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainConfigExceptionTest {
    @Test
    public void messageConstructorTest(){
        assertThrows(BlockchainConfigException.class, () -> {
            throw new BlockchainConfigException("Error");
        });
    }

    @Test
    public void messageThrowableConstructorTest(){
        assertThrows(BlockchainConfigException.class, () -> {
            throw new BlockchainConfigException("Error", new NullPointerException("throwable"));
        });
    }

}