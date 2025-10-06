package com.devsu.banking.account_movements.model.cqrs.command;

import com.devsu.banking.account_movements.model.cqrs.Command;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.MovementId;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;

import java.math.BigDecimal;

public record ChangeMovementCommand(
        MovementId movementId,
        MovementType type,
        AccountID accountNumber,
        AccountType accountType,
        BigDecimal amount
) implements Command {
}
