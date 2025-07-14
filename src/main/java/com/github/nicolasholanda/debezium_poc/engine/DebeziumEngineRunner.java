package com.github.nicolasholanda.debezium_poc.engine;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nicolasholanda.debezium_poc.model.UserChangeEventDTO;
import com.github.nicolasholanda.debezium_poc.service.UserCacheService;
import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DebeziumEngineRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DebeziumEngineRunner.class);

    private ExecutorService executor;
    private DebeziumEngine<ChangeEvent<String, String>> engine;

    @Autowired
    private Configuration userConnector;

    @Autowired
    private UserCacheService userCacheService;

    @Override
    public void run(String... args) {
        executor = Executors.newSingleThreadExecutor();

        engine = DebeziumEngine.create(Json.class)
                .using(userConnector.asProperties())
                .notifying(this::handleEvent)
                .build();

        executor.execute(engine);
    }

    private void handleEvent(ChangeEvent<String, String> event) {
        logger.info("Received Debezium event: {}", event.value());
        try {
            String json = event.value();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode root = mapper.readTree(json);
            JsonNode payloadNode = root.get("payload");
            UserChangeEventDTO dto = mapper.treeToValue(payloadNode, UserChangeEventDTO.class);

            switch (dto.op) {
                case "c": // create
                case "r": // read
                case "u": // update
                    userCacheService.updateCache(dto.after);
                    break;
                case "d": // delete
                    userCacheService.deleteFromCache(dto.before.id);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing Debezium event", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (engine != null) engine.close();
            if (executor != null) executor.shutdown();
        } catch (Exception e) {
            logger.error("Error shutting down Debezium engine", e);
        }
    }
}

