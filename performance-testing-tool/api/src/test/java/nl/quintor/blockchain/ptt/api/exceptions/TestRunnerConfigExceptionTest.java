package nl.quintor.blockchain.ptt.api.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestRunnerConfigExceptionTest {

    @Test
    public void messageConstructorTest(){
        assertThrows(TestRunnerConfigException.class, () -> {
            throw new TestRunnerConfigException("Error");
        });
    }

    @Test
    public void messageThrowableConstructorTest(){
        assertThrows(TestRunnerConfigException.class, () -> {
            throw new TestRunnerConfigException("Error", new NullPointerException("throwable"));
        });
    }


}