package com.devsu.banking.account_movements.usecase.registermovements.accounts;

import com.devsu.banking.account_movements.model.cqrs.command.ChangeAccountStatusCommand;
import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import reactor.core.publisher.Mono;

public class ChangeAccountDataUseCase {

    private final AccountsRepositoryGateway accountsRepositoryGateway;

    public ChangeAccountDataUseCase(AccountsRepositoryGateway accountsRepositoryGateway) {
        this.accountsRepositoryGateway = accountsRepositoryGateway;
    }


    public Mono<AccountSnapshot> handle(ChangeAccountStatusCommand changeAccountStatusCommand) {
        return Mono.just(changeAccountStatusCommand)
                .map(this::buildAccountDomain)
                .flatMap(accountsRepositoryGateway::updateAccountStatus);
    }

    private Account buildAccountDomain(ChangeAccountStatusCommand changeAccountStatusCommand) {
        return new Account(
                changeAccountStatusCommand.accountNumber().id(),
                changeAccountStatusCommand.status(),
                changeAccountStatusCommand.accountType()
        );
    }
}
