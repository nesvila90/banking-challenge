package com.devsu.banking.account_movements.model.cqrs.query;

import com.devsu.banking.account_movements.model.cqrs.Query;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;

public record FetchAccountByCustomerQuery(OwnerId customerId) implements Query { }
