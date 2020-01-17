package nl.quintor.blockchain.ptt.testrunners;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.quintor.blockchain.ptt.api.ITestRunnerFactory;
import nl.quintor.blockchain.ptt.api.exceptions.TestRunnerConfigException;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class EmptyTestRunnerFactory implements ITestRunnerFactory {

    private Logger logger = LoggerFactory.getLogger(EmptyTestRunnerFactory.class);

    @Override
    public Props createInstance(String testRunnerConfigString, ActorRef blockchainProvider) throws TestRunnerConfigException {
        EmptyTestRunnerConfig config = parseStringToConfig(testRunnerConfigString);

//        Create the testrunner Props object every parameters after EmptyTestRunner.class are given to a constructor of the EmptyTestRunner
        Props testrunner = Props.create(EmptyTestRunner.class, config, blockchainProvider);

        return testrunner;
    }

    //    Parse the string to the config object and ensure all the required values are set
    private EmptyTestRunnerConfig parseStringToConfig(String blockchainConfigString) {
        return null;
    }
}
