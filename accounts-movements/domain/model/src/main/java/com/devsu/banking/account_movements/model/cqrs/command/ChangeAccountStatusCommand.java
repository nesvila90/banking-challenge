package com.devsu.banking.account_movements.model.cqrs.command;

import com.devsu.banking.account_movements.model.cqrs.Command;
import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;

public record ChangeAccountStatusCommand(
        AccountID accountNumber,
        AccountType accountType,
        AccountStatus status
) implements Command {
}
