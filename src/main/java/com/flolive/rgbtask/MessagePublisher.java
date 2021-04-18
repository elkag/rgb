package com.flolive.rgbtask;

import com.flolive.rgbtask.dto.ColorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class MessagePublisher implements Consumer<FluxSink<ColorDto>> {
    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private final BlockingQueue<ColorDto> queue = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    public boolean push(ColorDto color) {
        return queue.offer(color);
    }

    @Override
    public void accept(FluxSink<ColorDto> sink) {
        this.executor.execute(() -> {
            while (true) {
                try {
                    final ColorDto colorDto = queue.take();

                    sink.next(colorDto);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
