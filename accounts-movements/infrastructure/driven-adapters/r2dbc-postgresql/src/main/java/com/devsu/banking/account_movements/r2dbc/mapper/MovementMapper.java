package com.devsu.banking.account_movements.r2dbc.mapper;

import com.devsu.banking.account_movements.model.entities.movements.MovementByAccount;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.r2dbc.entity.MovementEntity;
import com.devsu.banking.account_movements.r2dbc.projections.MovementsByAccountProjections;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        config = BaseMapper.class,
        uses = BaseMapper.class
)
public interface MovementMapper {


    @Mapping(source = "accountId", target = "accountDestiny.id")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "balanceAfter", target = "balance")
    @Mapping(source = "movementType", target = "type")
    @Mapping(source = "at", target = "date")
    Movements toModel(MovementEntity entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "type", target = "movementType")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "accountDestiny.id", target = "accountId")
    @Mapping(source = "balance", target = "balanceAfter")
    @Mapping(source = "date", target = "at")
    MovementEntity toEntity(Movements account);

    @Mapping(source = "accountId", target = "account.id.id")
    @Mapping(source = "accountType", target = "account.id.accountType")
    @Mapping(source = "accountType", target = "account.type")
    @Mapping(source = "accountNumber", target = "account.accountNumber")
    @Mapping(source = "status", target = "account.accountStatus", qualifiedByName = "convertStatus")
    @Mapping(source = "ownerId", target = "account.customerId")
    @Mapping(source = "currentBalance", target = "account.currentBalance")
    @Mapping(source = "initialBalance", target = "account.initialBalance")
    @BeanMapping(ignoreByDefault = true)
    MovementByAccount toModelByAccount(MovementsByAccountProjections movementsByAccount);


    MovementByAccount toModelByAccount(MovementEntity movementEntity);





    @Mapping(source = "movementId", target = "id")
    @Mapping(source = "movementType", target = "type")
    @Mapping(source = "currentBalance", target = "balance")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "at", target = "date")
    @Mapping(source = "movementAccountId", target = "accountDestiny")
    @BeanMapping(ignoreByDefault = true)
    Movements toModel(MovementsByAccountProjections movementsByAccount);


    void partialUpdate(Movements domain, @MappingTarget MovementEntity entity);

}
