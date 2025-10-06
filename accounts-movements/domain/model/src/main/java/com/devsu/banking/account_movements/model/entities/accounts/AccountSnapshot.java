package com.devsu.banking.account_movements.model.entities.accounts;

import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.CustomerId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountSnapshot {

    private AccountID id;
    private CustomerId customerId;
    private String accountNumber;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private AccountStatus accountStatus;
    private AccountType type;


}
