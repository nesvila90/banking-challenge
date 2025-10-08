package com.devsu.banking.account_movements.r2dbc.mapper;

import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.movements.MovementByAccount;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.r2dbc.entity.MovementEntity;
import com.devsu.banking.account_movements.r2dbc.projections.MovementsByAccountProjections;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        config = BaseMapper.class,
        uses = BaseMapper.class
)
public interface MovementMapper {

    @Mapping(target = "id", expression = "java(baseMapper.toAccountID(p.accountId(), p.accountType()))")
    @Mapping(target = "customerId", expression = "java(baseMapper.mapCustomerId(p.ownerId()))")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "initialBalance", source = "initialBalance")
    @Mapping(target = "currentBalance", source = "currentBalance")
    @Mapping(target = "accountStatus", source = "status", qualifiedByName = "convertStatus")
    @Mapping(target = "type", expression = "java(baseMapper.toAccountType(p.accountType()))")
    AccountSnapshot toAccountSnapshot(MovementsByAccountProjections p);

    @Mapping(target = "id", expression = "java(p.movementId() != null ? p.movementId().toString() : null)")
    @Mapping(target = "date", source = "at")
    @Mapping(target = "accountSource", expression = "java(baseMapper.toAccountID(p.movementAccountId(), p.accountType()))")
    @Mapping(target = "accountDestiny", expression = "java(null)") // no tenemos destino aquí
    @Mapping(target = "type", source = "p.movementType")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "balance", source = "balanceAfter")
    Movements toMovement(MovementsByAccountProjections p);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "date", source = "at")
    @Mapping(target = "type", source = "movementType")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "balance", source = "balanceAfter")
    @Mapping(target = "accountSource", expression = "java(baseMapper.toAccountID(entity.getAccountId(), null))")
    @Mapping(target = "accountDestiny", ignore = true)
    Movements toModel(MovementEntity entity);

    @InheritInverseConfiguration(name = "toModel")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "type", target = "movementType")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "accountDestiny.id", target = "accountId")
    @Mapping(source = "balance", target = "balanceAfter")
    @Mapping(source = "date", target = "at")
    MovementEntity toEntity(Movements domain);

    @BeanMapping(ignoreByDefault = true)
    void partialUpdate(Movements domain, @MappingTarget MovementEntity entity);

    @Named("groupByAccount")
    default List<MovementByAccount> groupByAccount(List<MovementsByAccountProjections> rows) {
        if (CollectionUtils.isEmpty(rows)) return Collections.emptyList();
        Map<UUID, List<MovementsByAccountProjections>> grouped = rows.stream()
                .collect(Collectors.groupingBy(MovementsByAccountProjections::accountId, LinkedHashMap::new, Collectors.toList()));

        var result = new ArrayList<MovementByAccount>(grouped.size());
        grouped.forEach((key, group) -> {
            var head = group.get(0);
            var account = toAccountSnapshot(head);
            var movements = group.stream()
                    .map(this::toMovement)
                    .toList();
            result.add(new MovementByAccount(account, movements));
        });
        return result;
    }

}
