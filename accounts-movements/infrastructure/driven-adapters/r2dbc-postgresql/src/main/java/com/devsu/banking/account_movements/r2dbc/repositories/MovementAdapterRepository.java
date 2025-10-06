package com.devsu.banking.account_movements.r2dbc.repositories;

import com.devsu.banking.account_movements.model.commons.exceptions.TechnicalException;
import com.devsu.banking.account_movements.model.commons.exceptions.messages.TechnicalErrorMessage;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import com.devsu.banking.account_movements.r2dbc.mapper.MovementMapper;
import com.devsu.banking.account_movements.r2dbc.repositories.movements.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
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




    //    private final AccountRepository accountRepository;
//    private final AccountMapper accountMapper;
//
//    @Override
//    public Mono<Account> findByNumber(String accountNumber, String accountType, Boolean status) {
//        return accountRepository.findByAccountNumberAndAccountTypeAndStatus(accountNumber, accountType, status)
//                .map(accountMapper::toDomain);
//    }
//
//    @Override
//    public Mono<Account> save(Account account) {
//        return Mono.just(account)
//                .map(accountMapper::toEntity)
//                .flatMap(accountRepository::save)
//                .map(accountMapper::toDomain);
//    }
//
//    public Mono<Account> updateAccount(Account account) {
//
//        var accountNumber = account.getId().id();
//        var accountType = account.getType().name();
//
//        var accountStatus = switch (account.getStatus()) {
//            case ACTIVE -> true;
//            case INACTIVE, SUSPENDED -> false;
//        };
//
//        return accountRepository.findByAccountNumberAndAccountTypeAndStatus(accountNumber, accountType, accountStatus)
//                .switchIfEmpty(Mono.error(new BusinessException(BusinessErrorMessage.ACCOUNT_NOT_FOUND)))
//                .doOnNext(accountFounded -> accountMapper.partialUpdate(account, accountFounded))
//                .flatMap(accountRepository::save)
//                .map(accountMapper::toDomain);
//    }
}
