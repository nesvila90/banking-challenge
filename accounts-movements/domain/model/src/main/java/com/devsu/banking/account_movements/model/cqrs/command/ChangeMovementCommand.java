package com.devsu.banking.account_movements.model.cqrs.command;

import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;

import java.math.BigDecimal;
import java.util.UUID;

public record ChangeMovementCommand(
        UUID movementId,
        MovementType type,
        AccountID accountNumber,
        AccountType accountType,
        BigDecimal amount
) {
}
