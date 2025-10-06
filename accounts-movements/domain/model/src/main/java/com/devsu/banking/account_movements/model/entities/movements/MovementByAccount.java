package com.devsu.banking.account_movements.model.entities.movements;

import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;

import java.util.List;

public record MovementByAccount(AccountSnapshot account, List<Movements> movements) {
}
