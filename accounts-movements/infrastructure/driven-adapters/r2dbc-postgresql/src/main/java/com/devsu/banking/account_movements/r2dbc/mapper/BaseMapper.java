package com.devsu.banking.account_movements.r2dbc.mapper;

import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.CustomerId;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
@MapperConfig(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG)
public interface BaseMapper {


    @Mapping(target = "accountType", ignore = true)
    @Mapping(source = "value", target = "id")
    AccountID mapAccountId(UUID value);

    @Mapping(source = "value", target = "id")
    @Mapping(source = "accountType", target = "accountType")
    AccountID mapAccountId(UUID value, AccountType accountType);

    @Mapping(source = "value", target = "id")
    CustomerId mapCustomerId(UUID value);

    @Named("convertBooleanStatus")
    default boolean convertStatus(AccountStatus status) {
        return switch (status) {
            case ACTIVE -> true;
            case INACTIVE, SUSPENDED -> false;
        };
    }

    @Named("convertStatus")
    default AccountStatus convertStatus(boolean status) {
        return status ? AccountStatus.ACTIVE : AccountStatus.SUSPENDED;
    }
}
