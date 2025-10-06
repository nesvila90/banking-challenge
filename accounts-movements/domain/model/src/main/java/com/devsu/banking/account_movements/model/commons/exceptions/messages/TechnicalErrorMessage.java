package com.devsu.banking.account_movements.model.commons.exceptions.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorMessage {

    UNEXPECTED_EXCEPTION("TE_0001", "Technical Unexpected error."),
    MANDATORY_TECHNICAL_PARAMETER_NOT_PRESENT("TE_0002", "Mandatory parameter not found."),
    CONCURRENCY_CONFLICT("TE_0003", "There's another process executing the same operation."),
    BAD_REQUEST("TE_0004", "Request invalid. Incomplete parameters or request malformed");

    private final String code;
    private final String message;
}
