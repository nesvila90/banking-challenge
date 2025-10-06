package com.devsu.banking.account_movements.model.commons.exceptions.dto;


import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ErrorDescription {

    String reason;
    String domain;
    String code;
    String message;
}
