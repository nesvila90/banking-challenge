package com.devsu.banking.account_movements.model.entities.accounts.gateways;

import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountsRepositoryGateway {

    Mono<AccountSnapshot> findActiveAccountsByNumberAndType(AccountID accountNumber);

    Flux<AccountSnapshot> findAccountsByCustomerId(OwnerId ownerId);

    Mono<AccountSnapshot> save(Account createAccountCommand);

    Mono<AccountSnapshot> updateAccountStatus(Account account);

    Mono<AccountSnapshot> updateAccount(Account changeAccountDataCommand);
}
