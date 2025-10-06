package com.devsu.banking.person_customer.model.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private UUID id;
    private String name;
    private String codeId;
    private String genre;
    private LocalDate age;
    private String address;
    private String telephone;
    private String personId;
}
