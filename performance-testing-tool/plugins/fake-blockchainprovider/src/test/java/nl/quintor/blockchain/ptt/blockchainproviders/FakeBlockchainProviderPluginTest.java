package nl.quintor.blockchain.ptt.blockchainproviders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FakeBlockchainProviderPluginTest {

    @Mock
    private Logger mockLogger;

    @Mock
    private PluginWrapper mockPlugin;

    private FakeBlockchainProviderPlugin sut;

    @BeforeEach
    void setup() {
        sut = new FakeBlockchainProviderPlugin(mockPlugin);
        sut.logger = mockLogger;
    }

    @Test
    void start() {
        sut.start();
        verify(mockLogger).info(any(String.class));
    }

    @Test
    void stop() {
        sut.stop();
        verify(mockLogger).info(any(String.class));
    }

    @Test
    void delete() {
        sut.delete();
        verify(mockLogger).info(any(String.class));
    }
}