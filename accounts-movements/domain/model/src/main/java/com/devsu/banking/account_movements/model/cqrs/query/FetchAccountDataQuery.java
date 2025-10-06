package com.devsu.banking.account_movements.model.cqrs.query;

import com.devsu.banking.account_movements.model.cqrs.Query;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;

public record FetchAccountDataQuery(AccountID accountID) implements Query {
}
