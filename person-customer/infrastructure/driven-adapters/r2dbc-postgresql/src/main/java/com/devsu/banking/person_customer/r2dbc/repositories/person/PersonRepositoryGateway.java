package com.devsu.banking.person_customer.r2dbc.repositories.person;

import com.devsu.banking.person_customer.r2dbc.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PersonRepositoryGateway extends ReactiveCrudRepository<PersonEntity, UUID> {

    Mono<PersonEntity> findByCodeId(String personId);

    Mono<Void> deleteByCodeId(String personId);

    Mono<Boolean> existsByCodeId(String codeId);
}
