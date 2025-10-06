package com.devsu.banking.account_movements.model.cqrs.query;

import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;

public record FetchAccountDataQuery(AccountID accountNumber, AccountType accountType) {
}
