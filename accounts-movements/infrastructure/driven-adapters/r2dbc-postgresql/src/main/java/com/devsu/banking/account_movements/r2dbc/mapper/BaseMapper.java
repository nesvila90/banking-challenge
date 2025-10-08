package com.devsu.banking.account_movements.r2dbc.mapper;

import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.CustomerId;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.Named;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
@MapperConfig(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG)
public interface BaseMapper {


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

    default AccountID toAccountID(UUID id, String accountType) {
        var type = Optional.ofNullable(accountType)
                .map(this::toAccountType)
                .orElse(null);
        if (id == null) return null;
        return new AccountID(id.toString(), type);
    }


    default AccountType toAccountType(String s) {
        return Optional.ofNullable(s)
                .map(AccountType::valueOf)
                .orElse(null);
    }

    default MovementType toMovementType(String s) {
        return Optional.ofNullable(s)
                .map(MovementType::fromValue)
                .orElse(null);
    }

    default String fromMovementType(MovementType t) {
        return t == null ? null : t.name();
    }

    default UUID fromAccountID(AccountID accountID) {
        if (accountID == null || accountID.id() == null) return null;
        return UUID.fromString(accountID.id());
    }

    @Named("statusToAccountStatus")
    default AccountStatus statusToAccountStatus(boolean status) {
        return status ? AccountStatus.ACTIVE : AccountStatus.INACTIVE;
    }
}
