package com.devsu.banking.account_movements.model.cqrs.query.list;

import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;

public record FetchMovementsByAccountQuery(AccountID accountID) {
}
