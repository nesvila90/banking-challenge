package com.devsu.banking.account_movements.r2dbc.repositories;

import com.devsu.banking.account_movements.model.commons.exceptions.TechnicalException;
import com.devsu.banking.account_movements.model.commons.exceptions.messages.TechnicalErrorMessage;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.movements.MovementByAccount;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import com.devsu.banking.account_movements.r2dbc.mapper.MovementMapper;
import com.devsu.banking.account_movements.r2dbc.repositories.movements.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MovementAdapterRepository implements MovementsRepositoryGateway {

    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;

    @Override
    public Mono<Movements> save(Movements movements) {
        return Mono.just(movements)
                .map(movementMapper::toEntity)
                .flatMap(movementRepository::save)
                .timeout(Duration.ofSeconds(5))
                .map(movementMapper::toModel);
    }

    @Override
    public Mono<Movements> updateMovement(Movements movements) {

        var id = Optional.ofNullable(movements)
                .map(Movements::getId)
                .map(UUID::fromString)
                .orElseThrow(() -> new TechnicalException(TechnicalErrorMessage.UNEXPECTED_EXCEPTION));

        return movementRepository.findById(id)
                .doOnNext(movementEntity -> movementMapper.partialUpdate(movements, movementEntity))
                .flatMap(movementRepository::save)
                .timeout(Duration.ofSeconds(5))
                .map(movementMapper::toModel);

    }

    @Override
    public Flux<MovementByAccount> fetchMovementByAccountId(AccountID accountID) {
        var accountId = UUID.fromString(accountID.id());
        return movementRepository.findByAccountId(accountId)
                .map(movementMapper::toModelByAccount);
    }

    @Override
    public Flux<MovementByAccount> fetchMovementsAccountsByOwner(UUID ownerId, LocalDate initialDate, LocalDate finalDate) {
        return movementRepository.findMovementsAccountsByUser(ownerId, initialDate, finalDate)
                .map(movementMapper::toModelByAccount);
    }

}
