package com.flolive.rgbtask.service;

import com.flolive.rgbtask.dto.ColorDto;
import com.flolive.rgbtask.dto.ColorMapper;
import com.flolive.rgbtask.data.Color;
import com.flolive.rgbtask.data.ColorRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ColorService {

    private final ColorRepository repository;
    private final ColorMapper mapper;

    public ColorService(ColorRepository repository, ColorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Flux<Color> save(final Flux<ColorDto> colorDto) {
        return colorDto.map(mapper::mapToInteger)
                .map(c -> Color.builder().value(c).build())
                .flatMap(repository::save);
    }
}
