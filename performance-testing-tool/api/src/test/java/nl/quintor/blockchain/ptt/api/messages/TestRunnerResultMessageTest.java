package nl.quintor.blockchain.ptt.api.messages;

import nl.quintor.blockchain.ptt.api.TransactionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestRunnerResultMessageTest {

    private TestRunnerResultMessage sut;

    @BeforeEach
    private void setup(){
        sut = new TestRunnerResultMessage();
    }

    @Test
    public void constructorTest(){
        assertNotNull(new TestRunnerResultMessage());
    }

    @Test
    public void resultListTest(){
        List<TransactionResult> transactionResults = new ArrayList<>();
        sut.setResultList(transactionResults);
        assertEquals(transactionResults,sut.getResultList());
    }

}