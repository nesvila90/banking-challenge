package com.devsu.banking.account_movements.r2dbc.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record MovementsByAccountProjections(
        //Accounts Properties
        UUID accountId,
        UUID ownerId,
        String accountNumber,
        String accountType,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        boolean status,
        int version,
        LocalDateTime accountCreatedAt,
        LocalDateTime accountUpdatedAt,
        //Movements Properties
        UUID movementId,
        UUID movementAccountId,
        LocalDateTime at,
        String movementType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        LocalDateTime movementCreatedAt
) {
}
