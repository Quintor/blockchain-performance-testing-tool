package nl.quintor.blockchain.ptt;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Tag("manual")
public class End2EndTests {

    @Test
    public void End2EndHyperledgerTest() {
        String[] arguments = {"-f", getClass().getClassLoader().getResource("hyperledgerTestRunner.yml").getPath()};
        assertDoesNotThrow(() -> PerformanceTestingToolStarter.main(arguments));
    }
    @Test
    public void End2EndRinkerbyTest() {
        String[] arguments = {"-f", getClass().getClassLoader().getResource("rinkerbyTestRunner.yml").getPath()};
        assertDoesNotThrow(() -> PerformanceTestingToolStarter.main(arguments));
    }

    @Test
    public void End2EndRinkerbyTestHeavy() {
        String[] arguments = {"-f", getClass().getClassLoader().getResource("rinkerbyTestScenarioHeavy.yml").getPath()};
        assertDoesNotThrow(() -> PerformanceTestingToolStarter.main(arguments));
    }

    @Test
    public void End2EndFiveTestRunnerTestOnGanache() {
        String[] arguments = {"-f", getClass().getClassLoader().getResource("fiveTestRunnersOnGanache.yml").getPath()};
        assertDoesNotThrow(() -> PerformanceTestingToolStarter.main(arguments));
    }
    @Test
    public void End2EndFiveTestRunnerTestOnGanacheGenerateYamlReport() {
        String[] arguments = {"-f", getClass().getClassLoader().getResource("fiveTestRunnersOnGanache.yml").getPath(), "-ft", "YAML"};
        assertDoesNotThrow(() -> PerformanceTestingToolStarter.main(arguments));
    }

}
