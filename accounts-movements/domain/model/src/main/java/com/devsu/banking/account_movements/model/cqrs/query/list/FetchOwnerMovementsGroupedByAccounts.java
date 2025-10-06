package com.devsu.banking.account_movements.model.cqrs.query.list;

import com.devsu.banking.account_movements.model.cqrs.Query;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;

import java.time.LocalDate;

public record FetchOwnerMovementsGroupedByAccounts(OwnerId ownerId, LocalDate initialDate, LocalDate finalDate) implements Query { }
