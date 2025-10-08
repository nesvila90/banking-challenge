package com.devsu.banking.account_movements.model.entities.movements.gateways;

import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.movements.MovementByAccount;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface MovementsRepositoryGateway {

    Mono<Movements> save(Movements account);

    Mono<Movements> updateMovement(Movements movements);

    Flux<Movements> fetchMovementByAccountId(AccountID accountID);

    Flux<MovementByAccount> fetchMovementsAccountsByOwner(UUID ownerId, LocalDate initialDate, LocalDate finalDate);
}
