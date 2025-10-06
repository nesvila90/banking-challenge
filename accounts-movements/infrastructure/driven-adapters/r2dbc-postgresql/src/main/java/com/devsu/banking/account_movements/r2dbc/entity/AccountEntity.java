package com.devsu.banking.account_movements.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("account")
public class AccountEntity {

    @Id
    private UUID id;
    @Column("owner_id")
    private UUID ownerId;                 // referencia externa
    @Column("account_number")
    private String accountNumber;
    @Column("account_type")
    private String accountType;       // SAVINGS | CHECKING
    @Column("initial_balance")
    private BigDecimal initialBalance;
    @Column("current_balance")
    private BigDecimal currentBalance;
    @Column("status")
    private Boolean status;
    @Version
    private Long version;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
