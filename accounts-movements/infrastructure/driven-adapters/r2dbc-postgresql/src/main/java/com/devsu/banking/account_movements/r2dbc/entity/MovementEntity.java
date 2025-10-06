package com.devsu.banking.account_movements.r2dbc.entity;

import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("movement")
public class MovementEntity {
    @Id
    private UUID id;
    @Column("account_id")
    private UUID accountId;
    @Column("at")
    private Instant at;
    @Column("movement_type")
    private MovementType movementType;     // DEPOSIT | WITHDRAW
    @Column("amount")
    private BigDecimal amount;              // positivo
    @Column("balance_after")
    private BigDecimal balanceAfter;
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
}
