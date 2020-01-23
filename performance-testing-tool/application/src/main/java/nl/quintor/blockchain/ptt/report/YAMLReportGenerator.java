package nl.quintor.blockchain.ptt.report;

import akka.actor.AbstractActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.quintor.blockchain.ptt.config.TestScenarioConfig;
import nl.quintor.blockchain.ptt.report.messages.PrintReportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class YAMLReportGenerator extends AbstractActor {

    private Logger logger = LoggerFactory.getLogger(YAMLReportGenerator.class);
    private ObjectMapper objectMapper;
    private String outputFileLocation;
    private TestScenarioConfig config;

    public YAMLReportGenerator(String outputFileLocation, TestScenarioConfig config) {
        this.outputFileLocation =  outputFileLocation;
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        this.config = config;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PrintReportMessage.class, this::generateReport)
                .build();
    }

    private void generateReport(PrintReportMessage message) {
        File outputFile = new File(this.outputFileLocation +".yml");
        File rawOutputFile = new File(this.outputFileLocation +"-raw.yml");
        try {
            objectMapper.writeValue(outputFile, message.getParsedResults());
            objectMapper.writeValue(rawOutputFile, message.getRawResults());
        } catch (IOException e) {
            logger.error("Failed generating yaml report", e);
        }finally {
            getContext().getSystem().terminate();
        }
    }
}
