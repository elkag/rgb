package com.flolive.rgbtask.dto;

import lombok.*;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class ColorDto {

    private Integer red;
    private Integer green;
    private Integer blue;
}
