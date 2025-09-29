package com.devsu.banking.person_customer.model.commons.exceptions;

import com.devsu.banking.person_customer.model.commons.exceptions.messages.BusinessErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {

    private final BusinessErrorMessage businessErrorMessage;

}
