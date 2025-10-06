package com.devsu.banking.account_movements.usecase.registermovements.accounts;

import com.devsu.banking.account_movements.model.cqrs.command.CreateAccountCommand;
import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import reactor.core.publisher.Mono;

public class CreateAccountUseCase {

    private final AccountsRepositoryGateway accountsRepositoryGateway;

    public CreateAccountUseCase(AccountsRepositoryGateway accountsRepositoryGateway) {
        this.accountsRepositoryGateway = accountsRepositoryGateway;
    }

    public Mono<AccountSnapshot> createAccount(CreateAccountCommand createAccountCommand) {
        var account = buildAccountDomain(createAccountCommand);
        return accountsRepositoryGateway.save(account);
    }

    private Account buildAccountDomain(CreateAccountCommand createAccountCommand) {
        var accountNumber = AccountNumberGeneratorUseCase.generateAccountNumber();
        return new Account(accountNumber, createAccountCommand.initialBalance(), createAccountCommand.status(), createAccountCommand.accountType(), null, new OwnerId(createAccountCommand.ownerId()));
    }
}
