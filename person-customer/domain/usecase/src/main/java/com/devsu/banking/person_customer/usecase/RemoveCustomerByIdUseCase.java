package com.devsu.banking.person_customer.usecase;

import com.devsu.banking.person_customer.model.cqrs.command.RemoveCustomerByIdCommand;
import com.devsu.banking.person_customer.model.customer.gateways.CustomerPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RemoveCustomerByIdUseCase {

    private final CustomerPersistenceGateway customerPersistenceGateway;

    public Mono<Void> execute(RemoveCustomerByIdCommand removeCustomerByIdCommand) {
        return customerPersistenceGateway.delete(removeCustomerByIdCommand).log();
    }
}
