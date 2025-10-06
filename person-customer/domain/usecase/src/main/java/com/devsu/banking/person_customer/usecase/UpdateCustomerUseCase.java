package com.devsu.banking.person_customer.usecase;

import com.devsu.banking.person_customer.model.cqrs.command.UpdateCustomerCommand;
import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.model.customer.gateways.CustomerPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerPersistenceGateway customerPersistenceGateway;

    public Mono<Customer> execute(UpdateCustomerCommand updateCustomerCommand) {
        return customerPersistenceGateway.update(updateCustomerCommand);
    }
}
