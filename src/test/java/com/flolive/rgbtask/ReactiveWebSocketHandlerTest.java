package com.flolive.rgbtask;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flolive.rgbtask.dto.ColorDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
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
    public void client() throws InterruptedException {
        this.socketClient.execute(
                URI.create("http://localhost:8080/ws"),
                session -> Flux.interval(Duration.ofMillis(100))
                        .map(String::valueOf)
                        .map(session::textMessage)
                        .as(session::send)
        );

        Thread.sleep(10000);
    }

    @Test
    void givenSentMessagesWhenReceiveAllResponsesThenSendCloseToClient() {

        ReplayProcessor<String> clientFlux = ReplayProcessor.create();

        Flux<String> toSend = Flux.range(1, 10).map(i -> Integer.toString(i));

        socketClient.execute(
                URI.create("ws://localhost:8080/ws"),
                session ->
                        Mono.when(
                                session
                                        .send(toSend.map(session::textMessage)),
                                session
                                        .receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .subscribeWith(clientFlux)
                                        .then()
                        )
        )
                .block(Duration.ofMillis(20000));

        StepVerifier.create(clientFlux)
                .expectNextCount(10)
                .verifyComplete();
    }

    private static final ObjectMapper objectMapper= new ObjectMapper();

    @Test
    public void color() throws Exception {
        int count = 1;
        String inputObject = objectMapper.writeValueAsString(new ColorDto(0, 0, 0));
        Flux<String> input = Flux.range(1, count).map(index -> inputObject);
        ReplayProcessor<Object> output = ReplayProcessor.create(count);

        socketClient.execute(
                URI.create("ws://localhost:8080/rgb"),
                session -> session
                        .send(input.map(session::textMessage))
                        //.thenReturn(session.receive().map(WebSocketMessage::getPayloadAsText))
                        .thenMany(session.receive().take(count).map(WebSocketMessage::getPayloadAsText))
                        .subscribeWith(output)
                        .then())
                .block(Duration.ofMillis(5000));

        assertEquals(input.collectList().block(Duration.ofMillis(5000)).get(0),
                output.collectList().block(Duration.ofMillis(5000)).get(0));
    }

    @Test
    public void testNotificationsOnUpdates() throws Exception {

        int count = 10;
        AtomicLong counter = new AtomicLong();
        URI uri = URI.create("ws://127.0.0.1:8080/rgb");

        socketClient.execute(uri, (WebSocketSession session) -> {

            Mono<WebSocketMessage> out = Mono.just(session.textMessage("{red: 0, green: 0, blue: 0}"));

            Flux<String> in = session
                    .receive()
                    .map(WebSocketMessage::getPayloadAsText);

            return session
                    .send(out)
                    .thenMany(in)
                    .doOnNext(str -> counter.incrementAndGet())
                    .then();

        }).subscribe();

        Thread.sleep(10);

        Assertions.assertEquals(counter.get(), count);
    }
}