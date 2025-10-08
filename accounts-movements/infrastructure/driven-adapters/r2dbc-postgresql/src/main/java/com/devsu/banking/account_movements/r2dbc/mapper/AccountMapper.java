package com.devsu.banking.account_movements.r2dbc.mapper;

import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.r2dbc.entity.AccountEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {

    @Mapping(source = "id.id", target = "id")
    @Mapping(source = "ownerId.ownerId", target = "ownerId")
    @Mapping(source = "type", target = "accountType")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "initialBalance", source = ".", qualifiedByName = "initialIfNew")
    @Mapping(target = "currentBalance", expression = "java(account.getBalance())")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AccountEntity toEntity(Account account);

    @Mapping(source = "id", target = "id.id")
    @Mapping(source = "accountType", target = "id.accountType")
    @Mapping(source = "ownerId", target = "customerId.id")
    @Mapping(source = "accountType", target = "type")
    @Mapping(source = "status", target = "accountStatus")
    AccountSnapshot toDomain(AccountEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "id.id", target = "id")             // si viene null, no sobreescribe
    @Mapping(source = "ownerId.ownerId", target = "ownerId")
    @Mapping(source = "type", target = "accountType")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "currentBalance", source = "balance")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void partialUpdate(Account account, @MappingTarget AccountEntity entity);


    @Named("initialIfNew")
    default BigDecimal initialIfNew(Account account) {
        boolean isNew = account.getId() == null || account.getId().id() == null;
        return isNew ? BigDecimal.ZERO : account.getBalance();
    }

    default String mapAccountType(AccountType t) {
        return t != null ? t.name() : null; // "SAVINGS" | "CHECKING"
    }

    @AfterMapping
    default void fixType(@MappingTarget AccountEntity target, Account source) {
        if (source != null && source.getType() != null) {
            target.setAccountType(source.getType().name());
        }
        if (source != null && source.getStatus() != null) {
            target.setStatus(source.getStatus() == AccountStatus.ACTIVE);
        }
    }


    default boolean convertStatus(AccountStatus status) {
        return switch (status) {
            case ACTIVE -> true;
            case INACTIVE, SUSPENDED -> false;
        };
    }

    default AccountStatus convertStatus(boolean status) {
        return status ? AccountStatus.ACTIVE : AccountStatus.SUSPENDED;
    }
}
