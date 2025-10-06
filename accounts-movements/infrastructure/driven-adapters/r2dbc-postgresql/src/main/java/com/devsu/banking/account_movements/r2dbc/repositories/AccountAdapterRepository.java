package com.devsu.banking.account_movements.r2dbc.repositories;

import com.devsu.banking.account_movements.model.commons.exceptions.BusinessException;
import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import com.devsu.banking.account_movements.r2dbc.mapper.AccountMapper;
import com.devsu.banking.account_movements.r2dbc.repositories.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.ACCOUNT_NOT_FOUND;
import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.CUSTOMER_NOT_FOUND;
import static java.lang.Boolean.TRUE;

@Repository
@RequiredArgsConstructor
public class AccountAdapterRepository implements AccountsRepositoryGateway {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Mono<AccountSnapshot> findActiveAccountsByNumberAndType(AccountID accountID) {
        return accountRepository.findByAccountNumberAndAccountTypeAndStatus(accountID.id(), accountID.accountType().name(), TRUE)
                .switchIfEmpty(Mono.error(new BusinessException(ACCOUNT_NOT_FOUND)))
                .map(accountMapper::toDomain);
    }

    @Override
    public Flux<AccountSnapshot> findAccountsByCustomerId(OwnerId ownerId) {
        return accountRepository.findByOwnerId(ownerId.ownerId())
                .timeout(Duration.ofSeconds(10))
                .map(accountMapper::toDomain);
    }

    @Override
    public Mono<AccountSnapshot> save(Account account) {
        return Mono.just(account)
                .map(accountMapper::toEntity)
                .flatMap(accountRepository::save)
                .log()
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(DataIntegrityViolationException.class, e -> Mono.error(new BusinessException(CUSTOMER_NOT_FOUND)))
                .map(accountMapper::toDomain);
    }

    @Override
    public Mono<AccountSnapshot> updateAccountStatus(Account account) {
        var accountNumber = account.getAccountNumber();
        var accountType = account.getType().name();

        return accountRepository.findByAccountNumberAndAccountType(accountNumber, accountType)
                .switchIfEmpty(Mono.error(new BusinessException(ACCOUNT_NOT_FOUND)))
                .doOnNext(accountFounded -> accountMapper.partialUpdate(account, accountFounded))
                .flatMap(accountRepository::save)
                .timeout(Duration.ofSeconds(5))
                .map(accountMapper::toDomain);
    }

    @Override
    public Mono<AccountSnapshot> updateAccount(Account account) {

        var accountNumber = account.getAccountNumber();
        var accountType = account.getType().name();

        var status = resolveStatus(account);
        return accountRepository.findByAccountNumberAndAccountTypeAndStatus(accountNumber, accountType, status)
                .switchIfEmpty(Mono.error(new BusinessException(ACCOUNT_NOT_FOUND)))
                .doOnNext(accountFounded -> accountMapper.partialUpdate(account, accountFounded))
                .flatMap(accountRepository::save)
                .timeout(Duration.ofSeconds(5))
                .map(accountMapper::toDomain);
    }

    //private methods
    private static Boolean resolveStatus(Account account) {
        return switch (account.getStatus()) {
            case ACTIVE -> true;
            case INACTIVE, SUSPENDED -> false;
        };
    }

}
