package com.devsu.banking.account_movements.model.commons.exceptions.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum BusinessErrorMessage {

    INVALID_STATUS("BU_0010", "Status not founded."),
    ACCOUNT_ALREADY_EXIST("BU_0011", "Account already exist."),
    CUSTOMER_NOT_FOUND("BU_0018", "Customer not founded."),
    ACCOUNT_NOT_FOUND("BU_0012", "Account not founded."),
    INACTIVE_ACCOUNT("BU_0013", "Account Inactive."),
    SUSPENDED_ACCOUNT("BU_0014", "Account Suspended."),
    INSUFFICIENT_FUNDS("BU_0015", "Insufficient Funds."),
    INVALID_AMOUNT("BU_0016", "Invalid amount."),
    INVALID_MOVEMENT_TYPE("BU_0017", "Invalid movement type."),
    MOVEMENT_NOT_FOUND("BU_0018", "Movement not founded."),
    MISSING_REQUIRED_FIELD("PB_0002", "Parameters not found");

    private final String code;
    private final String message;

}
