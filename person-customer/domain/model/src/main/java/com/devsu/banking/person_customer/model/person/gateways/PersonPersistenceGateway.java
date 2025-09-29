package com.devsu.banking.person_customer.model.person.gateways;

import com.devsu.banking.person_customer.model.commons.PageDTO;
import com.devsu.banking.person_customer.model.person.Person;

import java.util.UUID;

public interface PersonPersistenceGateway {

    Person save(Person person);

    Person findById(UUID id);

    Person update(Person person);

    void delete(UUID id);

    PageDTO<Person> findAll(int page, int size);
}
