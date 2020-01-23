package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.quintor.blockchain.ptt.api.ITestRunnerFactory;
import nl.quintor.blockchain.ptt.api.exceptions.TestRunnerConfigException;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Extension
public class LinearTestRunnerFactory implements ITestRunnerFactory {

    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(LinearTestRunnerFactory.class);

    @Override
    public Props createInstance(String testRunnerConfigString, ActorRef blockchainProvider) throws TestRunnerConfigException {
        if(objectMapper == null){
            objectMapper = new ObjectMapper(new YAMLFactory());
        }
        if(testRunnerConfigString == null){
            throw new TestRunnerConfigException("Config string can't be null");
        }
        LinearTestRunnerConfig testRunnerConfig;
        try{
            testRunnerConfig = objectMapper.readValue(testRunnerConfigString, LinearTestRunnerConfig.class);
            testRunnerConfig.validate();
        }catch(IOException e){
            throw new TestRunnerConfigException("Couldn't parse blockchain config", e);
        }
        return Props.create(LinearTestRunner.class, testRunnerConfig, blockchainProvider);
    }
}
