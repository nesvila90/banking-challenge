package com.devsu.banking.person_customer.model.cqrs.command;


import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.model.person.Person;

public record CreateCustomerCommand(Customer customer, Person person) {
}
