package com.devsu.banking.account_movements.usecase.registermovements.accounts;

import com.devsu.banking.account_movements.model.cqrs.query.FetchAccountDataQuery;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import reactor.core.publisher.Mono;

public class FetchActiveAccountUseCase {

    private final AccountsRepositoryGateway accountsRepositoryGateway;

    public FetchActiveAccountUseCase(AccountsRepositoryGateway accountsRepositoryGateway) {
        this.accountsRepositoryGateway = accountsRepositoryGateway;
    }

    public Mono<AccountSnapshot> handle(FetchAccountDataQuery query) {
        return accountsRepositoryGateway.findActiveAccountsByNumberAndType(query.accountID());
    }
}
