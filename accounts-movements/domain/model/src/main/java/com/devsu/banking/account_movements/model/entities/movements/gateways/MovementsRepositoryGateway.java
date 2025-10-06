package com.devsu.banking.account_movements.model.entities.movements.gateways;

import com.devsu.banking.account_movements.model.entities.movements.Movements;
import reactor.core.publisher.Mono;

public interface MovementsRepositoryGateway {

    Mono<Movements> save(Movements account);

}
