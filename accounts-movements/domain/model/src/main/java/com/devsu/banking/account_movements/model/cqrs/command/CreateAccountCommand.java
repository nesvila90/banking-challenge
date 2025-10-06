package com.devsu.banking.account_movements.model.cqrs.command;

import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountCommand(
        UUID ownerId,
        String accountNumber,
        AccountType accountType,
        BigDecimal initialBalance,
        AccountStatus status
) { }
