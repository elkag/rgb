package com.flolive.rgbtask.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.resources;

@Configuration
public class WebConfig {
    @Bean
    public RouterFunction<ServerResponse> indexRouter() {
        return resources("/**", new ClassPathResource("static/"));
    }
}
