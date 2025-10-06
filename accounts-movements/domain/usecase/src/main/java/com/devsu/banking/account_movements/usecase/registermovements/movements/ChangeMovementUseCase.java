package com.devsu.banking.account_movements.usecase.registermovements.movements;

import com.devsu.banking.account_movements.model.cqrs.command.ChangeMovementCommand;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import reactor.core.publisher.Mono;

public class ChangeMovementUseCase {

    private final MovementsRepositoryGateway movementRepositoryGateway;

    public ChangeMovementUseCase(MovementsRepositoryGateway movementRepositoryGateway) {
        this.movementRepositoryGateway = movementRepositoryGateway;
    }

    public Mono<Movements> handle(ChangeMovementCommand changeMovementCommand) {
        return Mono.just(changeMovementCommand)
                .map(this::mapMovementsToUpdate)
                .flatMap(movementRepositoryGateway::updateMovement);
    }

    private Movements mapMovementsToUpdate(ChangeMovementCommand changeMovementData) {
        var movementsToUpdate = new Movements();
        movementsToUpdate.setId(changeMovementData.movementId().id());
        movementsToUpdate.setType(changeMovementData.type());
        movementsToUpdate.setAmount(changeMovementData.amount());
        movementsToUpdate.setAccountSource(changeMovementData.accountNumber());
        return movementsToUpdate;
    }
}
