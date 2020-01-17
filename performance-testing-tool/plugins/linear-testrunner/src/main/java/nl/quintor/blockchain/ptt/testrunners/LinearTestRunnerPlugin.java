package nl.quintor.blockchain.ptt.testrunners;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinearTestRunnerPlugin extends Plugin {
    final Logger logger = LoggerFactory.getLogger(LinearTestRunnerPlugin.class);

    public LinearTestRunnerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("STARTED {}", LinearTestRunnerPlugin.class.toString());
    }

    @Override
    public void stop() {
        logger.info("STOPPED {}", LinearTestRunnerPlugin.class.toString());
    }

    @Override
    public void delete() {
        logger.info("DELETED {}", LinearTestRunnerPlugin.class.toString());
    }
}
