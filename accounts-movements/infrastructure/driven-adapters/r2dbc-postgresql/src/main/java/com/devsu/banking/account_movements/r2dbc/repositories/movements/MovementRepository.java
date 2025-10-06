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
            SELECT * FROM account a INNER JOIN public.movement m on a.id = m.account_id 
                     where a.owner_id = :ownerId and a.at between (:initialDate, :finalDate)
                     order by account_number, at desc
            """)
    Flux<MovementsByAccountProjections> findMovementsAccountsByUser(@Param("ownerId") UUID ownerId,
                                                                    @Param("initialDate") LocalDate initialDate,
                                                                    @Param("finalDate") LocalDate finalDate);

    Flux<MovementEntity> findByAccountId(UUID accountId);
}
