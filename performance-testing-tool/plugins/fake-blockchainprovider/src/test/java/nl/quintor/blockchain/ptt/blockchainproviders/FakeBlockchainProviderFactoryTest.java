package nl.quintor.blockchain.ptt.blockchainproviders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FakeBlockchainProviderFactoryTest {


    private static final String BLOCKCHAINCONFIG = "";
    @Mock
    private Logger mockLogger;

    private FakeBlockchainProviderFactory sut;

    @BeforeEach
    void setup(){
        sut = new FakeBlockchainProviderFactory();
        sut.logger = mockLogger;
    }

    @Test
    void createInstance() {
//        assertEquals(sut.createInstance(BLOCKCHAINCONFIG).getClass(), FakeBlockchainProvider.class);
    }
    @Test
    void verifyLog() {
//        sut.createInstance(BLOCKCHAINCONFIG);
//        verify(mockLogger).info(any(String.class));
    }
}