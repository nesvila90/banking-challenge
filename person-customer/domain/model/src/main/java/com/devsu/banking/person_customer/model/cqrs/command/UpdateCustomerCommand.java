package com.devsu.banking.person_customer.model.cqrs.command;

import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.model.person.Person;
import lombok.Builder;

@Builder
public record UpdateCustomerCommand (Person personData, Customer customerData, String codeId) { }
