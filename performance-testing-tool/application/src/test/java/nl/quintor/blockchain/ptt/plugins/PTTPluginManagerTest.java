import akka.actor.ActorRef;
import akka.actor.Props;
import nl.quintor.blockchain.ptt.api.IBlockchainProviderFactory;
import nl.quintor.blockchain.ptt.api.ITestRunnerFactory;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import nl.quintor.blockchain.ptt.api.exceptions.TestRunnerConfigException;
import nl.quintor.blockchain.ptt.plugins.PTTPluginManager;
import nl.quintor.blockchain.ptt.plugins.exceptions.PluginStartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class PTTPluginManagerTest {

    private static final String NORMAL_PROVID = "ethereum";
    private static final String NONEXISTANT_PROVID = "superfast";
    private static final Integer NEGATIVEINT = -1;
    private static final String INVALIDBLOCKCHAINCONFIGSTRING = "invalid";

    @InjectMocks
    private PTTPluginManager sut;

    @Mock
    private PluginManager mockPluginManager;
    @Mock
    private ActorRef mockBlockchainProvider;
    @Mock
    private IBlockchainProviderFactory mockBlockchainProviderFactory;
    @Mock
    private List<IBlockchainProviderFactory> blockchainFactoryList;

    private Props mockTestRunner = Props.empty();
    private Props mockBlockchainProviderProp = Props.empty();

    @Mock
    private ITestRunnerFactory mockTestRunnerFactory;
    @Mock
    private List<ITestRunnerFactory> testRunnerFactoryList;

    private String mockConfig;



    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    //    Constructor tests
    @Test
    public void givenConstructorPluginManagerParameterIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PTTPluginManager(null));
    }

    //    getBlockchainProvider tests
    @Test
    public void givenGetBlockchainProviderProviderIdIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getBlockchainProvider(null, mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderProviderIdIsEmptyThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getBlockchainProvider("", mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderProviderIdContainsOnlySpacesThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getBlockchainProvider(" ", mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderOrdinalIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, null, mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderOrdinalNegativeThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, NEGATIVEINT, mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderProviderIdIsNormalThenGetIBlockchainProvider() throws BlockchainConfigException, PluginStartException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(IBlockchainProviderFactory.class, NORMAL_PROVID)).thenReturn(blockchainFactoryList);
        when(blockchainFactoryList.get(any(Integer.class))).thenReturn(mockBlockchainProviderFactory);
        when(mockBlockchainProviderFactory.createInstance(mockConfig)).thenReturn(mockBlockchainProviderProp);
        Props blockchainProvider = sut.getBlockchainProvider(NORMAL_PROVID, mockConfig);
        assertEquals(mockBlockchainProviderProp, blockchainProvider);
    }

    @Test
    public void givenGetBlockchainProviderVerifyStartPluginIsCalledWithProviderId() throws BlockchainConfigException, PluginStartException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(IBlockchainProviderFactory.class, NORMAL_PROVID)).thenReturn(blockchainFactoryList);
        when(blockchainFactoryList.get(any(Integer.class))).thenReturn(mockBlockchainProviderFactory);
        when(mockBlockchainProviderFactory.createInstance(mockConfig)).thenReturn(mockBlockchainProviderProp);
        sut.getBlockchainProvider(NORMAL_PROVID, mockConfig);
        verify(mockPluginManager).startPlugin(NORMAL_PROVID);
    }

    @Test
    public void givenGetBlockchainProviderProviderIdIsNotFoundThenThrowPluginStartException() {
        when(mockPluginManager.startPlugin(NONEXISTANT_PROVID)).thenThrow(new IllegalArgumentException("Unknown pluginId " + NONEXISTANT_PROVID));
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NONEXISTANT_PROVID, mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderPluginStateIsStoppedThenThrowPluginStartException() {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STOPPED);
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, mockConfig));
    }
    @Test
    public void givenGetBlockchainProviderPluginStateIsDisabledThenThrowPluginStartException() {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.DISABLED);
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, mockConfig));
    }
    @Test
    public void givenGetBlockchainProviderPluginStateIsCreatedThenThrowPluginStartException() {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.CREATED);
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, mockConfig));
    }
    @Test
    public void givenGetBlockchainProviderPluginStateIsResolvedThenThrowPluginStartException() {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.RESOLVED);
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, mockConfig));
    }
    @Test
    public void givenGetBlockchainProviderPluginStateIsStartedThenGetBlockchainProvider() throws BlockchainConfigException, PluginStartException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(IBlockchainProviderFactory.class, NORMAL_PROVID)).thenReturn(blockchainFactoryList);
        when(blockchainFactoryList.get(any(Integer.class))).thenReturn(mockBlockchainProviderFactory);
        when(mockBlockchainProviderFactory.createInstance(mockConfig)).thenReturn(mockBlockchainProviderProp);
        assertEquals(mockBlockchainProviderProp, sut.getBlockchainProvider(NORMAL_PROVID, mockConfig));
    }

    @Test
    public void givenGetBlockchainProviderFactoryGaveNullThenThrowNullPointerException() throws BlockchainConfigException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(IBlockchainProviderFactory.class, NORMAL_PROVID)).thenReturn(blockchainFactoryList);
        when(blockchainFactoryList.get(any(Integer.class))).thenReturn(mockBlockchainProviderFactory);
        when(mockBlockchainProviderFactory.createInstance(mockConfig)).thenReturn(null);
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, mockConfig));
    }
    @Test
    public void givenInvalidBlockchainConfigThenThrowPluginStartException() throws BlockchainConfigException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(IBlockchainProviderFactory.class, NORMAL_PROVID)).thenReturn(blockchainFactoryList);
        when(blockchainFactoryList.get(any(Integer.class))).thenReturn(mockBlockchainProviderFactory);
        when(mockBlockchainProviderFactory.createInstance(INVALIDBLOCKCHAINCONFIGSTRING)).thenThrow(new BlockchainConfigException("Invalid Config given"));
        assertThrows(PluginStartException.class, () -> sut.getBlockchainProvider(NORMAL_PROVID, INVALIDBLOCKCHAINCONFIGSTRING));
    }

    //    getTestRunner tests
    @Test
    public void givenGetTestRunnerTestRunnerIdIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getTestRunner(null, mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerTestRunnerIdIsEmptyThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getTestRunner("", mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerTestRunnerIdContainsOnlySpacesThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getTestRunner(" ", mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerOrdinalIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getTestRunner(NORMAL_PROVID, null, mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerOrdinalNegativeThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getTestRunner(NORMAL_PROVID, NEGATIVEINT, mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerBlockchainProviderIsNullThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sut.getTestRunner(NORMAL_PROVID, mockConfig, null));
    }

    @Test
    public void givenGetTestRunnerPluginIsNotFoundThenThrowPluginStartException() throws TestRunnerConfigException {
        when(mockPluginManager.startPlugin(NONEXISTANT_PROVID)).thenThrow(new IllegalArgumentException("Unknown pluginId " + NONEXISTANT_PROVID));
        assertThrows(PluginStartException.class, () -> sut.getTestRunner(NONEXISTANT_PROVID, mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerPluginStateIsNotStartedThenThrowPluginStartException() throws TestRunnerConfigException {
        when(mockPluginManager.startPlugin(NONEXISTANT_PROVID)).thenReturn(PluginState.STOPPED);
        assertThrows(PluginStartException.class, () -> sut.getTestRunner(NONEXISTANT_PROVID, mockConfig, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerFactoryCreatesNullThenThrowPluginStartException() throws TestRunnerConfigException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(ITestRunnerFactory.class, NORMAL_PROVID)).thenReturn(testRunnerFactoryList);
        when(testRunnerFactoryList.get(any(Integer.class))).thenReturn(mockTestRunnerFactory);
        when(mockTestRunnerFactory.createInstance(mockConfig, mockBlockchainProvider)).thenReturn(null);
        assertThrows(PluginStartException.class, () -> sut.getTestRunner(NORMAL_PROVID, mockConfig, mockBlockchainProvider));

    }

    @Test
    public void givenGetTestRunnerFactoryWithInvalidConfigThenThrowPluginStartException() throws TestRunnerConfigException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(ITestRunnerFactory.class, NORMAL_PROVID)).thenReturn(testRunnerFactoryList);
        when(testRunnerFactoryList.get(any(Integer.class))).thenReturn(mockTestRunnerFactory);
        when(mockTestRunnerFactory.createInstance(INVALIDBLOCKCHAINCONFIGSTRING, mockBlockchainProvider)).thenThrow(new TestRunnerConfigException("Config invalid"));
        assertThrows(PluginStartException.class, () -> sut.getTestRunner(NORMAL_PROVID, INVALIDBLOCKCHAINCONFIGSTRING, mockBlockchainProvider));
    }

    @Test
    public void givenGetTestRunnerWithValidParametersThenGetTestRunner() throws TestRunnerConfigException, PluginStartException {
        when(mockPluginManager.startPlugin(NORMAL_PROVID)).thenReturn(PluginState.STARTED);
        when(mockPluginManager.getExtensions(ITestRunnerFactory.class, NORMAL_PROVID)).thenReturn(testRunnerFactoryList);
        when(testRunnerFactoryList.get(any(Integer.class))).thenReturn(mockTestRunnerFactory);
        when(mockTestRunnerFactory.createInstance(mockConfig, mockBlockchainProvider)).thenReturn(mockTestRunner);
        Props testRunner = sut.getTestRunner(NORMAL_PROVID, mockConfig, mockBlockchainProvider);
        assertNotNull(testRunner);
    }
}