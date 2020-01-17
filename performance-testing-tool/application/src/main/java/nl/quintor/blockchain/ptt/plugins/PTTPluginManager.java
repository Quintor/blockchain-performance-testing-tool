package nl.quintor.blockchain.ptt.plugins;

import akka.actor.ActorRef;
import akka.actor.Props;
import nl.quintor.blockchain.ptt.api.IBlockchainProviderFactory;
import nl.quintor.blockchain.ptt.api.ITestRunnerFactory;
import nl.quintor.blockchain.ptt.api.exceptions.BlockchainConfigException;
import nl.quintor.blockchain.ptt.api.exceptions.TestRunnerConfigException;
import nl.quintor.blockchain.ptt.plugins.exceptions.PluginStartException;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;

import java.util.List;

public class PTTPluginManager {

    private PluginManager pluginManager;


    public PTTPluginManager(PluginManager pluginManager) {
        if(pluginManager == null){
            throw new IllegalArgumentException("PluginManager can't be null");
        }
        pluginManager.loadPlugins();
        this.pluginManager = pluginManager;
    }

    public Props getBlockchainProvider(String providerId, Integer ordinal, String blockchainConfig) throws PluginStartException {
        checkCommonParameters(providerId, ordinal);
        List<IBlockchainProviderFactory> blockchainProviderFactoryList = pluginManager.getExtensions(IBlockchainProviderFactory.class, providerId);
        if(blockchainProviderFactoryList.isEmpty()){
            throw new PluginStartException("Plugin with id {} found but contains no IBlockchainFactory classes");
        }
        IBlockchainProviderFactory factory = blockchainProviderFactoryList.get(ordinal);
        Props blockchainProvider;
        try{
            blockchainProvider = factory.createInstance(blockchainConfig);
        }catch(BlockchainConfigException e){
            throw new PluginStartException(e.getMessage());
        }
        if(blockchainProvider == null){
            throw new PluginStartException("Factory made a null BlockchainProvider");
        }
        return blockchainProvider;
    }

    public Props getBlockchainProvider(String providerId, String blockchainConfig) throws PluginStartException {
        return getBlockchainProvider(providerId, 0, blockchainConfig);
    }

    public Props getTestRunner(String testRunnerId, String testRunnerConfig, ActorRef blockchainProvider) throws PluginStartException{
        return getTestRunner(testRunnerId, 0, testRunnerConfig, blockchainProvider);
    }

    public Props getTestRunner(String testRunnerId, Integer ordinal, String testRunnerConfig, ActorRef blockchainProvider) throws PluginStartException{
        if(blockchainProvider == null){
            throw new IllegalArgumentException("A valid Blockchainprovider must be given");
        }
        checkCommonParameters(testRunnerId, ordinal);
        List<ITestRunnerFactory> testRunnerFactoryList = pluginManager.getExtensions(ITestRunnerFactory.class, testRunnerId);
        if(testRunnerFactoryList.isEmpty()){
            throw new PluginStartException("Plugin with id {} found but contains no ITestRunnerFactory classes");
        }
        ITestRunnerFactory factory = testRunnerFactoryList.get(ordinal);
        Props testRunner;
        try{
            testRunner = factory.createInstance(testRunnerConfig, blockchainProvider);
        }catch(TestRunnerConfigException e){
            throw new PluginStartException(e.getMessage());
        }
        if(testRunner == null){
            throw new PluginStartException("Factory made a null Test Runner");
        }
        return testRunner;
    }

    private void checkCommonParameters(String pluginId, Integer ordinal) throws PluginStartException {
        if (pluginId == null || pluginId.isBlank()) {
            throw new IllegalArgumentException("providerId can't be null or empty");
        }
        if(ordinal == null || ordinal <= -1){
            throw new IllegalArgumentException("ordinal can't be null or a negative number");
        }
        PluginState pluginState;
        try{
            pluginState = pluginManager.startPlugin(pluginId);
        }catch(IllegalArgumentException e){
            throw new PluginStartException("Plugin can't be started because " + e.getMessage());
        }
        if(!PluginState.STARTED.equals(pluginState)){
            throw new PluginStartException("Plugin can't be started with providerId " + pluginId + " Pluginstate:"+pluginState);
        }
    }
}
