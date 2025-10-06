package com.devsu.banking.account_movements.usecase.registermovements.movements;

import com.devsu.banking.account_movements.model.commons.exceptions.BusinessException;
import com.devsu.banking.account_movements.model.cqrs.command.RegisterMovementCommand;
import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountSnapshot;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import com.devsu.banking.account_movements.model.entities.accounts.policy.OverdraftPolicy;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.ACCOUNT_NOT_FOUND;
import static com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage.INSUFFICIENT_FUNDS;
import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
public class RegisterMovementsUseCase {

    private final AccountsRepositoryGateway accountsRepositoryGateway;
    private final MovementsRepositoryGateway movementRepositoryGateway;



    public Mono<Movements> handle(RegisterMovementCommand registerMovementCommand) {
        var accountNumber = registerMovementCommand.accountNumber();
        var accountType = registerMovementCommand.accountType();
        return accountsRepositoryGateway.findActiveAccountsByNumberAndType(accountNumber, accountType)
                .switchIfEmpty(Mono.error(new BusinessException(ACCOUNT_NOT_FOUND)))
                .map(Account::from)
                .doOnNext(this::addOverdraftPolicy)
                .map(account -> account.applyMovement(registerMovementCommand, account.getId()))
                .flatMap(this::upsert)
                .map(Tuple2::getT2);
    }

    private Mono<Tuple2<AccountSnapshot, Movements>> upsert(Tuple2<Account, Movements> accountMovement) {
        return Mono.zip(
                accountsRepositoryGateway.updateAccount(accountMovement.getT1()),
                movementRepositoryGateway.save(accountMovement.getT2())
        );
    }

    private void addOverdraftPolicy(Account account) {
        account.setOverdraftPolicy(validateOverdraftPolicy());
    }

    private OverdraftPolicy validateOverdraftPolicy() {
        return balance -> {
            if (ZERO.compareTo(balance) > 0) throw new BusinessException(INSUFFICIENT_FUNDS);
        };
    }
}
