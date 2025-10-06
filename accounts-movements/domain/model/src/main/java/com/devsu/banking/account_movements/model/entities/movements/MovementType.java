package com.devsu.banking.account_movements.model.entities.movements;

import com.devsu.banking.account_movements.model.commons.exceptions.BusinessException;
import com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage;

import java.util.Arrays;

public enum MovementType {

    DEPOSIT,
    WITHDRAW;

    public static MovementType fromValue(String value) {
        return Arrays.stream(MovementType.values())
                .filter(type -> type.name().equalsIgnoreCase(value)).findFirst()
                .orElseThrow(() -> new BusinessException(BusinessErrorMessage.INVALID_MOVEMENT_TYPE));
    }
}
