package com.devsu.banking.person_customer.model.commons;

import lombok.Builder;

@Builder(toBuilder = true)
public record PageMetadata(Long totalElements, int totalPages, int number, int size) {

}
