package com.devsu.banking.person_customer.r2dbc.repositories.customer;

import com.devsu.banking.person_customer.r2dbc.entity.CustomerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CustomerRepositoryGateway extends ReactiveCrudRepository<CustomerEntity, UUID>, ReactiveSortingRepository<CustomerEntity, UUID> {

    Mono<CustomerEntity> findByPerson_CodeId(String codeId);
}
