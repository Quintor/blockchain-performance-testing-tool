package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HyperledgerBlockchainProviderFactoryTest {

    private static HyperledgerBlockchainProviderConfig config;
    private static String configString;

    private HyperledgerBlockchainProviderFactory sut;


    @BeforeAll
    private static void setConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        config = objectMapper.readValue(HyperledgerBlockchainProviderFactoryTest.class.getClassLoader().getResourceAsStream("testBlockchainProviderConfigBlock.yml"), HyperledgerBlockchainProviderConfig.class);
        configString = objectMapper.writeValueAsString(config);
    }

    @Test
    public void creatInstanceNoErrors(){
        sut = new HyperledgerBlockchainProviderFactory();
        assertDoesNotThrow(() -> {
            Props blockchainProvider = sut.createInstance(configString);
        });
    }
}