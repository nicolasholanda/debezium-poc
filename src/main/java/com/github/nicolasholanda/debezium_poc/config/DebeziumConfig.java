package com.github.nicolasholanda.debezium_poc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebeziumConfig {

    @Value("${debezium.db.host}")
    private String userDbHost;
    @Value("${debezium.db.port}")
    private String userDbPort;
    @Value("${debezium.db.user}")
    private String userDbUsername;
    @Value("${debezium.db.password}")
    private String userDbPassword;
    @Value("${debezium.db.name}")
    private String userDbName;

    @Bean
    public io.debezium.config.Configuration userConnector() {
        return io.debezium.config.Configuration.create()
                .with("name", "user-postgres-connector")
                .with("topic.prefix", "user-postgres-db-server")
                .with("plugin.name", "pgoutput")
                .with("publication.name", "users_pub")
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/offsets.dat")
                .with("offset.flush.interval.ms", "60000")
                .with("database.hostname", userDbHost)
                .with("database.port", userDbPort)
                .with("database.user", userDbUsername)
                .with("database.password", userDbPassword)
                .with("database.dbname", userDbName)
                .with("database.include.list", userDbName)
                .with("include.schema.changes", "true")
                .with("database.server.id", "10181")
                .with("database.server.name", "user-postgres-db-server")
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", "/tmp/dbhistory.dat")
                .with("poll.interval.ms", "5000")
                .build();
    }
}
