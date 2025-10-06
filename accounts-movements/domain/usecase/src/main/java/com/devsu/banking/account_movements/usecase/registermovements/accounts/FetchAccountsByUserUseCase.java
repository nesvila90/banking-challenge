package com.devsu.banking.account_movements.usecase.registermovements.accounts;

import com.devsu.banking.account_movements.model.cqrs.query.FetchAccountByCustomerQuery;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import reactor.core.publisher.Flux;

public class FetchAccountsByUserUseCase {

    private final AccountsRepositoryGateway accountsRepositoryGateway;

    public FetchAccountsByUserUseCase(AccountsRepositoryGateway accountsRepositoryGateway) {
        this.accountsRepositoryGateway = accountsRepositoryGateway;
    }

    public Flux<AccountSnapshot> handle(FetchAccountByCustomerQuery query) {
        return accountsRepositoryGateway.findAccountsByCustomerId(query.customerId());
    }
}
