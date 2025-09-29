package com.devsu.banking.person_customer.r2dbc.repositories;

import com.devsu.banking.person_customer.model.command.CreateCustomerCommand;
import com.devsu.banking.person_customer.model.command.RemoveCustomerByIdCommand;
import com.devsu.banking.person_customer.model.command.UpdateCustomerCommand;
import com.devsu.banking.person_customer.model.commons.exceptions.BusinessException;
import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.model.customer.gateways.CustomerPersistenceGateway;
import com.devsu.banking.person_customer.model.query.GetCustomerByIdQuery;
import com.devsu.banking.person_customer.r2dbc.entity.CustomerEntity;
import com.devsu.banking.person_customer.r2dbc.entity.PersonEntity;
import com.devsu.banking.person_customer.r2dbc.mapper.CustomerMapper;
import com.devsu.banking.person_customer.r2dbc.mapper.PersonMapper;
import com.devsu.banking.person_customer.r2dbc.repositories.customer.CustomerRepositoryGateway;
import com.devsu.banking.person_customer.r2dbc.repositories.person.PersonRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static com.devsu.banking.person_customer.model.commons.exceptions.messages.BusinessErrorMessage.CUSTOMER_ALREADY_EXIST;
import static com.devsu.banking.person_customer.model.commons.exceptions.messages.BusinessErrorMessage.CUSTOMER_NOT_FOUND;
import static com.devsu.banking.person_customer.model.commons.exceptions.messages.BusinessErrorMessage.PERSON_ASSOCIATED_TO_CUSTOMER_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerPersistenceGateway {

    private final PersonRepositoryGateway personRepositoryGateway;
    private final CustomerRepositoryGateway customerRepositoryGateway;
    private final TransactionalOperator transactionalOperator;

    private final PersonMapper personMapper;
    private final CustomerMapper customerMapper;


    public Mono<Customer> create(CreateCustomerCommand createCustomerCommand) {
        final var personToSave = personMapper.toEntity(createCustomerCommand.person());
        final var customerToSave = customerMapper.toEntity(createCustomerCommand.customer());

        return validatePersonDoesNotExist(personToSave.getCodeId())
                .then(savePerson(personToSave))
                .flatMap(savedPerson -> saveCustomer(customerToSave, savedPerson))
                .map(customerAndPerson -> customerMapper.toModel(customerAndPerson.getT1(), customerAndPerson.getT2()))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Customer> update(UpdateCustomerCommand dto) {
        var codeId = dto.codeId();
        return personRepositoryGateway.findByCodeId(codeId)
                .switchIfEmpty(Mono.error(new BusinessException(CUSTOMER_NOT_FOUND)))
                .flatMap(person ->
                        customerRepositoryGateway.findByPerson_CodeId(person.getCodeId())
                                .switchIfEmpty(Mono.error(new BusinessException(PERSON_ASSOCIATED_TO_CUSTOMER_NOT_FOUND)))
                                .flatMap(customer -> {
                                    personMapper.partialUpdate(dto.personData(), person);
                                    customerMapper.partialUpdate(dto.customerData(), customer);
                                    return Mono.zip(
                                            personRepositoryGateway.save(person),
                                            customerRepositoryGateway.save(customer)
                                    );
                                })
                )
                .map(tuple -> customerMapper.toModel(tuple.getT2(), tuple.getT1()))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Customer> find(GetCustomerByIdQuery getCustomerByIdQuery) {
        var codeId = getCustomerByIdQuery.codeId();
        return personRepositoryGateway.findByCodeId(codeId)
                .switchIfEmpty(Mono.error(new BusinessException(CUSTOMER_NOT_FOUND)))
                .flatMap(person ->
                        customerRepositoryGateway.findByPerson_CodeId(person.getCodeId())
                                .switchIfEmpty(Mono.error(new BusinessException(PERSON_ASSOCIATED_TO_CUSTOMER_NOT_FOUND)))
                                .zipWith(Mono.just(person))
                ).map(tuple -> customerMapper.toModel(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Mono<Void> delete(RemoveCustomerByIdCommand removeCustomerByIdCommand) {
        var codeId = removeCustomerByIdCommand.codeId();
        return Mono.just(codeId)
                .flatMap(customerRepositoryGateway::findByPerson_CodeId)
                .map(CustomerEntity::getId)
                .flatMap(customerRepositoryGateway::deleteById)
                .then(personRepositoryGateway.deleteByCodeId(codeId))
                .as(transactionalOperator::transactional);
    }

    //private methods
    private Mono<Void> validatePersonDoesNotExist(String codeId) {
        return personRepositoryGateway.findByCodeId(codeId)
                .hasElement()
                .flatMap(exists -> Boolean.TRUE.equals(exists) ? Mono.error(new BusinessException(CUSTOMER_ALREADY_EXIST)) : Mono.empty());
    }

    private Mono<PersonEntity> savePerson(PersonEntity person) {
        return personRepositoryGateway.save(person);
    }

    private Mono<Tuple2<CustomerEntity, PersonEntity>> saveCustomer(CustomerEntity customer, PersonEntity person) {
        customer.setPersonId(person.getId());
        //        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepositoryGateway.save(customer).zipWith(Mono.just(person));
    }

}
