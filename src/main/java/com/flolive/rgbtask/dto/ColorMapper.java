package com.flolive.rgbtask.dto;

import com.flolive.rgbtask.data.Color;
import org.springframework.stereotype.Component;

@Component
public class ColorMapper {

    public ColorDto mapToModel(Color entity) {

        int value = entity.getValue();
        var red =   (value) & 255;
        var green = ( value >>  8 ) & 255;
        var blue =  ( value >> 16 ) & 255;

        return ColorDto.builder()
                .red(red)
                .green(green)
                .blue(blue).build();
    }

    public int mapToInteger(ColorDto model) {
        return 65536 * model.getRed() + 256 * model.getGreen() + model.getBlue();
    }
}
