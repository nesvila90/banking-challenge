package com.devsu.banking.account_movements.usecase.registermovements.factory;

import com.devsu.banking.account_movements.model.cqrs.command.RegisterMovementCommand;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class MockFactory {

    private MockFactory() {
    }

    public static Mono<AccountSnapshot> buildInactiveSavingsAccountSnapshot(String accountNumber, BigDecimal initialBalance, BigDecimal balance) {
        var type = AccountType.SAVINGS;
        var accountID = new AccountID(accountNumber, type);
        return Mono.just(buildAccountSnapshot(initialBalance, balance, accountID, type, AccountStatus.INACTIVE));
    }


    public static Mono<AccountSnapshot> buildActiveSavingsAccountSnapshot(String accountNumber, BigDecimal initialBalance, BigDecimal balance) {
        var type = AccountType.SAVINGS;
        var accountID = new AccountID(accountNumber, type);
        return Mono.just(buildAccountSnapshot(initialBalance, balance, accountID, type, AccountStatus.ACTIVE));
    }


    public static AccountSnapshot buildAccountSnapshot(BigDecimal initialBalance, BigDecimal balance, AccountID accountID, AccountType type, AccountStatus status) {
        AccountSnapshot accountSnapshot = new AccountSnapshot();
        accountSnapshot.setId(accountID);
        accountSnapshot.setType(type);
        accountSnapshot.setAccountStatus(status);
        accountSnapshot.setCurrentBalance(balance);
        accountSnapshot.setInitialBalance(initialBalance);
        return accountSnapshot;
    }


    public static RegisterMovementCommand cmd(MovementType type, AccountID accountNumber, AccountType accountType, BigDecimal amount) {
        return new RegisterMovementCommand(type, accountNumber, accountType, amount);
    }

    public static Movements buildMovement(String id, BigDecimal amount, BigDecimal balance, AccountID accountNumber, MovementType movementType) {
        Movements data = new Movements();
        data.setId(id);
        data.setAmount(amount);
        data.setType(movementType);
        data.setBalance(balance);
        data.setAccountDestiny(accountNumber);
        data.setDate(LocalDateTime.now());
        return data;
    }


}
