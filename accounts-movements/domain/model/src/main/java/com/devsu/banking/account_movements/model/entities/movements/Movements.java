package com.devsu.banking.account_movements.model.entities.movements;

import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Movements {

    private String id;
    private LocalDateTime date;
    private AccountID accountSource;
    private AccountID accountDestiny;
    private MovementType type;
    private BigDecimal amount;
    private BigDecimal balance;

}
