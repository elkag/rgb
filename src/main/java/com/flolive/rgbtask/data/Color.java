package com.flolive.rgbtask.data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Builder @Getter
@Table("colors")
public class Color {

    @Id
    private final Long id;
    private final Integer value;

}
