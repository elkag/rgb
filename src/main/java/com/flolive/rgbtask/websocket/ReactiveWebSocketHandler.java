package com.flolive.rgbtask.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flolive.rgbtask.dto.ColorDto;
import com.flolive.rgbtask.dto.ColorMapper;
import com.flolive.rgbtask.service.ColorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;


@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(ReactiveWebSocketHandler.class);

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ColorService service;
    private final ColorMapper colorMapper;

    public ReactiveWebSocketHandler(ColorService service, ColorMapper colorMapper) {
        this.service = service;
        this.colorMapper = colorMapper;
    }

    @Override
    @NonNull
    public Mono<Void> handle(WebSocketSession session) {

        Flux<ColorDto> colorDto = session.receive()
                .doOnSubscribe(subscription -> logger.info("Joining web socket... sessionId: " + session.getId()))
                .doOnError(e -> logger.error("Service failed for session id: {}, message: ", session.getId()))
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::getMessage)
                .doFinally(sig -> logger.info("Leaving web socket... sessionId: " + session.getId()));

        Flux<String> output = service.save(colorDto)
                .map(colorMapper::mapToModel)
                .map(c -> {
                    String result = "";
                    try {
                        result = jsonMapper.writeValueAsString(c);
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage());
                    }
                    return result;
                })
                .doOnError(e -> logger.error("Error occurred while sending message to client.", e));;

        return session.send(output.map(session::textMessage));
    }


    public ColorDto getMessage(String message) {
        ColorDto colorMessage = new ColorDto();
        try{
            colorMessage = jsonMapper.readValue(message, ColorDto.class);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return colorMessage;
    }

}
