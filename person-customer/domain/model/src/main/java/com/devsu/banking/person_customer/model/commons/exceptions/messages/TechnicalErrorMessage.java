package com.devsu.banking.person_customer.model.commons.exceptions.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorMessage {

    UNEXPECTED_EXCEPTION("CMT0006", "Error inesperado"),
    BAD_REQUEST("CMT0007", "Request invalid. Incomplete parameters or request malformed");

    private final String code;
    private final String message;
}
