package com.devsu.banking.person_customer.r2dbc.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persona", schema = "public") // Un nombre de tabla más convencional
public class PersonEntity {

    @Id
    private UUID id;

    @NotBlank
    private String name;

    @NotNull
    private Gender genre;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    private String codeId;

    @NotBlank
    private String address;

    @NotBlank
    private String telephone;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
