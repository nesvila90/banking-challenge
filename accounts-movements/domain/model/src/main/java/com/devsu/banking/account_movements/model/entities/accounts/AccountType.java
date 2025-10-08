package com.devsu.banking.account_movements.model.entities.accounts;

public enum AccountType {
    SAVINGS,
    CHECKING;

    public static AccountType from(String type) {
        return AccountType.valueOf(type.toUpperCase());
    }
}
