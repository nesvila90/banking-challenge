package com.devsu.banking.person_customer.model.commons.exceptions.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum BusinessErrorMessage {

    INVALID_STATUS("BU_0001", "Status not founded."),
    CUSTOMER_ALREADY_EXIST("BU_0001", "Customer already exist."),
    CUSTOMER_NOT_FOUND("BU_0002", "Customer not founded."),
    PERSON_NOT_FOUND("BU_0003", "Person not founded."),
    PERSON_ASSOCIATED_TO_CUSTOMER_NOT_FOUND("BU_0003", "Associated Person Customer not founded."),
    MISSING_REQUIRED_FIELD("PB_0002", "Parameters not found");

    private final String code;
    private final String message;

}
