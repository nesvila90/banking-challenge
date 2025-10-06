package com.devsu.banking.person_customer.r2dbc.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cliente", schema = "public")
public class CustomerEntity {
    @Id
    private UUID id;

    @NotNull
    @Column("person_id")
    private UUID personId;

    @Transient
    private PersonEntity person;

    @NotBlank
    @Column("username")
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Boolean status = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
