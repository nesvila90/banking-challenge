package com.devsu.banking.account_movements.model.entities.accounts.policy;

import java.math.BigDecimal;

@FunctionalInterface
public interface OverdraftPolicy {

    void validate(BigDecimal totalBalance);
}
