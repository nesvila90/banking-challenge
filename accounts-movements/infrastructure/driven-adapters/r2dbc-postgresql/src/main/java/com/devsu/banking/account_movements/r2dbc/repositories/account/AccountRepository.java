package com.devsu.banking.account_movements.r2dbc.repositories.account;

import com.devsu.banking.account_movements.r2dbc.entity.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, UUID>, ReactiveSortingRepository<AccountEntity, UUID> {

    Mono<AccountEntity> findByAccountNumberAndAccountTypeAndStatus(String accountNumber, String accountType, Boolean status);

    Mono<AccountEntity> findByAccountNumberAndAccountType(String accountNumber, String accountType);

    Flux<AccountEntity> findByOwnerId(UUID uuid);
}
