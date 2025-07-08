package com.github.nicolasholanda.debezium_poc.engine;

import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
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

    @Autowired
    private Configuration userConnector;

    @Override
    public void run(String... args) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                    .using(userConnector.asProperties())
                    .notifying(this::handleEvent)
                    .build();
            executor.execute(engine);
        }
    }

    private void handleEvent(ChangeEvent<String, String> event) {
        logger.info("Received Debezium event: {}", event.value());
    }
}

