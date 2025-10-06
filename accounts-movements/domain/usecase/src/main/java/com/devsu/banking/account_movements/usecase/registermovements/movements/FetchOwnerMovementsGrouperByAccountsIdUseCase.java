package com.devsu.banking.account_movements.usecase.registermovements.movements;

import com.devsu.banking.account_movements.model.cqrs.query.list.FetchOwnerMovementsGroupedByAccounts;
import com.devsu.banking.account_movements.model.entities.movements.MovementByAccount;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import reactor.core.publisher.Flux;

public class FetchOwnerMovementsGrouperByAccountsIdUseCase {

    private final MovementsRepositoryGateway movementRepositoryGateway;

    public FetchOwnerMovementsGrouperByAccountsIdUseCase(MovementsRepositoryGateway movementRepositoryGateway) {
        this.movementRepositoryGateway = movementRepositoryGateway;
    }

    public Flux<MovementByAccount> handle(FetchOwnerMovementsGroupedByAccounts query) {
        var ownerId = query.ownerId().ownerId();
        var initialDate = query.initialDate();
        var finalDate = query.finalDate();
        return movementRepositoryGateway.fetchMovementsAccountsByOwner(ownerId, initialDate, finalDate);
    }
}
