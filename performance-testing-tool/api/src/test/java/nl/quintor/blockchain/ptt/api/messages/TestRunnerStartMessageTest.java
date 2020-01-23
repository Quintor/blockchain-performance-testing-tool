package nl.quintor.blockchain.ptt.api.messages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestRunnerStartMessageTest {


    @Test
    public void constructorTest(){
        assertNotNull(new TestRunnerStartMessage());
    }
}