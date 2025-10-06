package com.devsu.banking.person_customer.r2dbc.mapper;

import com.devsu.banking.person_customer.model.customer.Customer;
import com.devsu.banking.person_customer.r2dbc.entity.CustomerEntity;
import com.devsu.banking.person_customer.r2dbc.entity.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface CustomerMapper {


    Customer toModel(CustomerEntity entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "userId", source = "entity.username")
    @Mapping(source = "personEntity", target = "personData")
    @Mapping(source = "entity.createdAt", target = "createdAt")
    @Mapping(source = "entity.updatedAt", target = "updatedAt")
    Customer toModel(CustomerEntity entity, PersonEntity personEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "personId", source = "personData.personId")
    @Mapping(target = "username", source = "userId")
    @Mapping(source = "personData.age", target = "person.dateOfBirth")
    CustomerEntity toEntity(Customer model);

    void partialUpdate(Customer dto, @MappingTarget CustomerEntity entity);
}
