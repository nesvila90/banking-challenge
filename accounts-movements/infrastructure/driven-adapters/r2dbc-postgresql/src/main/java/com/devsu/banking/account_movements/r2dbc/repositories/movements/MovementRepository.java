package com.devsu.banking.account_movements.r2dbc.repositories.movements;

import com.devsu.banking.account_movements.r2dbc.entity.MovementEntity;
import com.devsu.banking.account_movements.r2dbc.projections.MovementsByAccountProjections;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface MovementRepository extends ReactiveCrudRepository<MovementEntity, UUID>, ReactiveSortingRepository<MovementEntity, UUID> {

    @Query(value = """ 
            SELECT
              a.id               AS account_id,
              a.owner_id         AS owner_id,
              a.account_number   AS account_number,
              a.account_type     AS account_type,
              a.initial_balance  AS initial_balance,
              a.current_balance  AS current_balance,
              a.status           AS status,
              a.version          AS version,
              a.created_at       AS account_created_at,
              a.updated_at       AS account_updated_at,
            
              m.id               AS movement_id,
              m.account_id       AS movement_account_id,
              m.at               AS at,
              m.movement_type    AS movement_type,
              m.amount           AS amount,
              m.balance_after    AS balance_after,
              m.created_at       AS movement_created_at
            FROM public.account a
            JOIN public.movement m ON a.id = m.account_id
            WHERE a.owner_id = :ownerId
              AND m.at BETWEEN :initialDate AND :finalDate
            ORDER BY a.account_number, m.at DESC;
            
            """)
    Flux<MovementsByAccountProjections> findMovementsAccountsByUser(@Param("ownerId") UUID ownerId,
                                                                    @Param("initialDate") LocalDate initialDate,
                                                                    @Param("finalDate") LocalDate finalDate);

    Flux<MovementEntity> findByAccountId(UUID accountId);
}
