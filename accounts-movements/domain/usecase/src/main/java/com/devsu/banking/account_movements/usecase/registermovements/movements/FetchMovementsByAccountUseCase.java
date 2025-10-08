package com.devsu.banking.account_movements.usecase.registermovements.movements;

import com.devsu.banking.account_movements.model.cqrs.query.list.FetchMovementsByAccountQuery;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import reactor.core.publisher.Flux;

public class FetchMovementsByAccountUseCase {

    private final MovementsRepositoryGateway movementRepositoryGateway;

    public FetchMovementsByAccountUseCase(MovementsRepositoryGateway movementRepositoryGateway) {
        this.movementRepositoryGateway = movementRepositoryGateway;
    }

    public Flux<Movements> handle(FetchMovementsByAccountQuery fetchMovementsByAccountQuery) {
        return movementRepositoryGateway.fetchMovementByAccountId(fetchMovementsByAccountQuery.accountID());
    }
}
