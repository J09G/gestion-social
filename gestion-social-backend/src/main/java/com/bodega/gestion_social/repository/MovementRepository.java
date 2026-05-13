package com.bodega.gestion_social.repository;

import com.bodega.gestion_social.model.Movement;
import com.bodega.gestion_social.model.MovementType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementRepository extends MongoRepository<Movement, String> {

    List<Movement> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Movement> findByTypeAndTimestampBetween(MovementType type, LocalDateTime start, LocalDateTime end);

    List<Movement> findByProductId(String productId);
}
