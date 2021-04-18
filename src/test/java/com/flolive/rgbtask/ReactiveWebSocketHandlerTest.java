package com.flolive.rgbtask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReactiveWebSocketHandlerTest {

    private WebSocketClient socketClient;

    @BeforeEach
    public void init(){
        this.socketClient = new ReactorNettyWebSocketClient();
    }

    @Test
    public void contextTest() {

        String key = "key";

        Mono<String> mono = Mono.just("anything")
                .flatMap(s -> Mono.deferContextual(Mono::just)
                        .map(ctx -> "Value stored in context: " + ctx.get(key)))
                .contextWrite(ctx -> ctx.put(key, "myValue"));

        StepVerifier.create(mono)
                .expectNext("Value stored in context: myValue")
                .verifyComplete();

    }
}