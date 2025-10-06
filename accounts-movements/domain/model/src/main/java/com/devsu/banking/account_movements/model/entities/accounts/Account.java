package com.devsu.banking.account_movements.model.entities.accounts;

import com.devsu.banking.account_movements.model.commons.exceptions.BusinessException;
import com.devsu.banking.account_movements.model.cqrs.command.RegisterMovementCommand;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.CustomerId;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import com.devsu.banking.account_movements.model.entities.accounts.policy.OverdraftPolicy;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import lombok.Getter;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.INACTIVE_ACCOUNT;
import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.INSUFFICIENT_FUNDS;
import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.INVALID_AMOUNT;
import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.INVALID_MOVEMENT_TYPE;
import static com.devsu.banking.account_movements.model.entities.movements.MovementType.DEPOSIT;
import static java.math.BigDecimal.ZERO;

public class Account {
    @Getter
    private final AccountStatus status;
    @Getter
    private final AccountType type;
    @Getter
    private final AccountID id;
    @Getter
    private final OwnerId ownerId;
    @Getter
    private final String accountNumber;
    @Getter
    private BigDecimal balance;
    private OverdraftPolicy overdraftPolicy;

    public static Account from(AccountSnapshot snapshot) {
        if (snapshot == null) return null;
        var initialBalance = Optional.ofNullable(snapshot.getInitialBalance()).orElse(ZERO);
        var balance = Optional.ofNullable(snapshot.getCurrentBalance())
                .orElse(initialBalance);

        var ownerId = Optional.ofNullable(snapshot.getCustomerId())
                .map(CustomerId::id)
                .map(UUID::fromString)
                .map(OwnerId::new)
                .orElse(null);

        // Construcción del agregado
        return new Account(
                snapshot.getAccountNumber(),
                balance,
                snapshot.getAccountStatus(),   // AccountStatus
                snapshot.getType(),            // AccountType
                snapshot.getId(),              // AccountID
                ownerId                        // OwnerId
        );
    }

    public Account(String accountNumber,
                   BigDecimal balance,
                   AccountStatus status,
                   AccountType type,
                   AccountID accountID,
                   OwnerId ownerId) {
        this.id = accountID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.type = type;
        this.ownerId = ownerId;
    }

    public void setOverdraftPolicy(OverdraftPolicy overdraftPolicy) {
        this.overdraftPolicy = overdraftPolicy;
    }

    public Tuple2<Account, Movements> applyMovement(RegisterMovementCommand command, AccountID sourceAccountId) {
        var amount = Optional.ofNullable(command.amount())
                .orElseThrow(() -> new BusinessException(INVALID_AMOUNT));
        var movementType = Optional.ofNullable(command.type())
                .orElseThrow(() -> new BusinessException(INVALID_MOVEMENT_TYPE));

        validateAccountToAddMovement();
        calculateNewBalance(balance, amount, movementType);
        validateOverdraft();
        var movementAdded = addMovement(sourceAccountId, movementType, amount);
        return Tuples.of(this, movementAdded);
    }

    private Movements addMovement(AccountID accountId, MovementType type, BigDecimal amount) {
        if (amount.signum() <= 0) throw new BusinessException(INVALID_AMOUNT);
        return Movements.builder()
                .accountDestiny(accountId)
                .type(type)
                .amount(amount)
                .balance(balance)
                .date(LocalDateTime.now())
                .build();
    }

    private void validateAccountToAddMovement() {
        if (!status.equals(AccountStatus.ACTIVE)) throw new BusinessException(INACTIVE_ACCOUNT);
        if (balance == null || ZERO.equals(balance)) throw new BusinessException(INSUFFICIENT_FUNDS);
    }

    private void validateOverdraft() {
        overdraftPolicy.validate(balance);
    }

    private void calculateNewBalance(BigDecimal balance, BigDecimal amount, MovementType type) {
        this.balance = type.equals(DEPOSIT) ? balance.add(amount) : balance.subtract(amount);
    }

}
