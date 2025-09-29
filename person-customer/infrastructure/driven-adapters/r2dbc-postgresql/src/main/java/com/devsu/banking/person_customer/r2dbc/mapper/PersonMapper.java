package com.devsu.banking.person_customer.r2dbc.mapper;

import com.devsu.banking.person_customer.model.person.Person;
import com.devsu.banking.person_customer.r2dbc.entity.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    Person toModel(PersonEntity entity);

    @Mapping(source = "codeId", target = "codeId")
    PersonEntity toEntity(Person model);

    void partialUpdate(Person dto, @MappingTarget PersonEntity entity);

}
