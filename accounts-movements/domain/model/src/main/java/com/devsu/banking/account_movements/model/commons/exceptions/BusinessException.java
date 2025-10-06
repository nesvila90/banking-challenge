package com.devsu.banking.account_movements.model.commons.exceptions;

import com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorMessage businessErrorMessage;

    public BusinessException(BusinessErrorMessage businessErrorMessage) {
        this.businessErrorMessage = businessErrorMessage;
    }

}
