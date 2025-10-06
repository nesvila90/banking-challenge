package com.devsu.banking.account_movements.r2dbc.mapper;

import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.r2dbc.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface MovementMapper {


    @Mapping(source = "accountId", target = "accountDestiny.id")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "balanceAfter", target = "balance")
    @Mapping(source = "movementType", target = "type")
    Movements toModel(MovementEntity entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "type", target = "movementType")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "accountDestiny.id", target = "accountId")
    @Mapping(source = "balance", target = "balanceAfter")
    MovementEntity toEntity(Movements account);

    void partialUpdate(Movements domain, @MappingTarget MovementEntity entity);


}
