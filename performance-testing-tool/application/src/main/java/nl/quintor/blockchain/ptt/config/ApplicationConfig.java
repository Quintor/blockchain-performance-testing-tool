package nl.quintor.blockchain.ptt.config;

import java.time.Duration;

public class ApplicationConfig {
    private String filePath;
    private String output;
    private OUTPUT_FORMAT outputFormat;
    private Duration timeout;

    public OUTPUT_FORMAT getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OUTPUT_FORMAT outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
}
