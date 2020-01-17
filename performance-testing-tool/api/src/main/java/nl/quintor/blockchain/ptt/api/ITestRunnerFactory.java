package nl.quintor.blockchain.ptt.api;

import akka.actor.ActorRef;
import akka.actor.Props;
import nl.quintor.blockchain.ptt.api.exceptions.TestRunnerConfigException;
import org.pf4j.ExtensionPoint;

public interface ITestRunnerFactory extends ExtensionPoint {

    Props createInstance(String testRunnerConfig, ActorRef blockchainProvider) throws TestRunnerConfigException;
}
