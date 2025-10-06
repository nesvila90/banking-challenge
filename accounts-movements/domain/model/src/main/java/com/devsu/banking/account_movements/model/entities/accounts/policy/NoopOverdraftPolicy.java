package com.devsu.banking.account_movements.model.entities.accounts.policy;

import java.math.BigDecimal;

public class NoopOverdraftPolicy implements OverdraftPolicy {
    @Override
    public void validate(BigDecimal totalBalance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
