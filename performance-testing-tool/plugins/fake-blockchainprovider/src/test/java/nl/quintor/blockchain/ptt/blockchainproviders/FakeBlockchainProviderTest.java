package nl.quintor.blockchain.ptt.blockchainproviders;

import nl.quintor.blockchain.ptt.api.TransactionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ExtendWith(MockitoExtension.class)
class FakeBlockchainProviderTest {

    private static final String BLOCKCHAINCONFIG = "";
    private static final String FUNCTIONID = "";
    @Mock
    private Logger mockLogger;

    private FakeBlockchainProvider sut;

    @BeforeEach
    void setup() {
//        sut = new FakeBlockchainProvider(BLOCKCHAINCONFIG);
//        sut.logger = mockLogger;
    }

    @Test
    void setupNetwork() {
//        sut.setupNetwork();
//        verify(mockLogger).info(any(String.class));
    }

    @Test
    void sendTransaction() {

//        assertEquals(sut.sendTransaction(FUNCTIONID).getClass(), TransactionResult.class);
    }

    @Test
    void sendTransactionVerifyLogging() {
//        sut.sendTransaction(FUNCTIONID);
//        verify(mockLogger, times(2)).info(any(String.class));
    }
}