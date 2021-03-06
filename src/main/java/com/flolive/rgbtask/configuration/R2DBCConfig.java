package com.flolive.rgbtask.configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.flolive.rgbtask.entities")
public class R2DBCConfig {
    @Bean
    public ConnectionFactory connectionFactory() {

        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgres")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "root")
                .option(PASSWORD, "root")
                .option(DATABASE, "rgb")
                .build());
    }
}
