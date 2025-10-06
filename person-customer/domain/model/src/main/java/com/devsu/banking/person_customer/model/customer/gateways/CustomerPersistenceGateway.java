package com.devsu.banking.person_customer.model.customer.gateways;

import com.devsu.banking.person_customer.model.cqrs.command.CreateCustomerCommand;
import com.devsu.banking.person_customer.model.cqrs.command.RemoveCustomerByIdCommand;
import com.devsu.banking.person_customer.model.cqrs.command.UpdateCustomerCommand;
import com.devsu.banking.person_customer.model.cqrs.query.GetCustomerByIdQuery;
import com.devsu.banking.person_customer.model.customer.Customer;
import reactor.core.publisher.Mono;

public interface CustomerPersistenceGateway {

    Mono<Customer> create(CreateCustomerCommand createCustomerCommand);

    Mono<Customer> update(UpdateCustomerCommand updateCustomerCommand);

    Mono<Customer> find(GetCustomerByIdQuery getCustomerByIdQuery);

    Mono<Void> delete(RemoveCustomerByIdCommand removeCustomerByIdCommand);
}
