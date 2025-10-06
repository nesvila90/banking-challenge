package com.devsu.banking.person_customer.usecase;

import com.devsu.banking.person_customer.model.cqrs.query.GetCustomerByIdQuery;
import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.model.customer.gateways.CustomerPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetCustomerByIdUseCase {

    private final CustomerPersistenceGateway customerPersistenceGateway;

    public Mono<Customer> execute(GetCustomerByIdQuery getCustomerByIdQuery) {
        return customerPersistenceGateway.find(getCustomerByIdQuery).log();
    }

}
