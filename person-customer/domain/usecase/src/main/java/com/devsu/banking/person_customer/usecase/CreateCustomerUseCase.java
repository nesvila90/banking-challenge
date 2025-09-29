package com.devsu.banking.person_customer.usecase;

import com.devsu.banking.person_customer.model.command.CreateCustomerCommand;
import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.model.customer.gateways.CustomerPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerPersistenceGateway customerPersistenceGateway;

    public Mono<Customer> execute(CreateCustomerCommand createCustomerCommand) {
        return customerPersistenceGateway.create(createCustomerCommand).log();
    }
}
