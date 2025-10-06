package com.devsu.banking.account_movements.r2dbc.repositories.movements;

import com.devsu.banking.account_movements.r2dbc.entity.MovementEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface MovementRepository extends ReactiveCrudRepository<MovementEntity, UUID>, ReactiveSortingRepository<MovementEntity, UUID> {

    Flux<MovementEntity> findMovementsEntitiesByAccountId(UUID accountId);
}
