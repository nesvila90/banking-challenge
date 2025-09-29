package com.devsu.banking.person_customer.r2dbc.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationSortUtils {

    public static Sort resolveSort(RelationalMappingContext mappingCtx, Class<?> entityClass, Sort requested) {
        if (requested == null || requested.isUnsorted()) return Sort.unsorted();
        var pe = mappingCtx.getRequiredPersistentEntity(entityClass);
        var safe = new ArrayList<Order>();
        for (Sort.Order o : requested) {
            var pp = pe.getPersistentProperty(o.getProperty());
            if (pp == null) {
                throw new IllegalArgumentException("Propiedad inválida para ordenar: " + o.getProperty());
            }
            String column = pp.getColumnName().getReference();
            safe.add(new Sort.Order(o.getDirection(), column));
        }
        return Sort.by(safe);
    }

    public static String resolveColumn(RelationalMappingContext mappingCtx, Class<?> entityClass, String property) {
        var pe = mappingCtx.getRequiredPersistentEntity(entityClass);
        var pp = pe.getPersistentProperty(property);
        if (pp == null) throw new IllegalArgumentException("Propiedad inválida: " + property);
        return pp.getColumnName().getReference();
    }


}
