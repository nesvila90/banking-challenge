package com.devsu.banking.person_customer.model.commons.exceptions;

import com.devsu.banking.person_customer.model.commons.exceptions.messages.TechnicalErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TechnicalException extends RuntimeException {

    private final TechnicalErrorMessage technicalErrorMessage;


}
