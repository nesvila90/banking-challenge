package com.devsu.banking.account_movements.model.commons.exceptions.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class ErrorResponse {

    List<ErrorDescription> errors;

}
