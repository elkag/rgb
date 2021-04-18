package com.flolive.rgbtask.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends ReactiveCrudRepository<Color, Integer> {
}
