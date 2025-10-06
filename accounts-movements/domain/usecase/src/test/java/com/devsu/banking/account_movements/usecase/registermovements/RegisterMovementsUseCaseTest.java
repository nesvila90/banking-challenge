package com.devsu.banking.account_movements.usecase.registermovements;

import com.devsu.banking.account_movements.model.commons.exceptions.BusinessException;
import com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage;
import com.devsu.banking.account_movements.model.cqrs.command.RegisterMovementCommand;
import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountStatus;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterMovementsUseCaseTest {

    @Mock
    AccountsRepositoryGateway accountsRepo;

    @Mock
    MovementsRepositoryGateway movementsRepo;

    @InjectMocks
    RegisterMovementsUseCase useCase;

    private Account activeAccount(BigDecimal balance, AccountType type) {
        return createAccount(balance, AccountStatus.ACTIVE, type);
    }

    private Account inactiveAccount(BigDecimal balance, AccountType type) {
        return createAccount(balance, AccountStatus.INACTIVE, type);
    }

    private Account createAccount(BigDecimal balance, AccountStatus status, AccountType type) {
        return new Account(new AccountID("ACC-OK"), balance, status, type, new OwnerId(UUID.randomUUID()));
    }

    private RegisterMovementCommand cmd(MovementType type, AccountID accountNumber, AccountType accountType, BigDecimal amount) {
        return new RegisterMovementCommand(type, accountNumber, accountType, amount);
    }

    @Test
    void deposit_ok_persiste_movimiento_y_cuenta() {
        // Given
        var initial = new BigDecimal("100.00");
        var amount = new BigDecimal("50.00");
        var acc = activeAccount(initial, AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-1")).thenReturn(Mono.just(acc));
        // guardados: devolvemos lo mismo (o con id si quieres)
        when(accountsRepo.save(any(Account.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(movementsRepo.save(any(Movements.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var command = cmd(MovementType.DEPOSIT, new AccountID("ACC-1"), AccountType.SAVINGS, amount);

        // When / Then
        StepVerifier.create(useCase.handle(command))
                .assertNext(saved -> {
                    assertEquals(MovementType.DEPOSIT, saved.getType());
                    assertEquals(0, saved.getAmount().compareTo(amount));
                    assertEquals(0, saved.getBalance().compareTo(new BigDecimal("150.00")));
                    assertNotNull(saved.getDate());
                })
                .verifyComplete();

        verify(accountsRepo, times(1)).findActiveAccountsByNumberAndType("ACC-1");
        verify(accountsRepo, times(1)).save(any(Account.class));
        verify(movementsRepo, times(1)).save(any(Movements.class));
        verifyNoMoreInteractions(accountsRepo, movementsRepo);
    }

    @Test
    void withdraw_ok_con_fondos_suficientes() {
        var acc = activeAccount(new BigDecimal("100.00"), AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-2")).thenReturn(Mono.just(acc));
        when(accountsRepo.save(any(Account.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(movementsRepo.save(any(Movements.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var command = cmd(MovementType.WITHDRAW, new AccountID("ACC-1"), AccountType.SAVINGS, new BigDecimal("40.00"));

        StepVerifier.create(useCase.handle(command))
                .assertNext(saved -> {
                    assertEquals(MovementType.WITHDRAW, saved.getType());
                    assertEquals(0, saved.getAmount().compareTo(new BigDecimal("40.00")));
                    assertEquals(0, saved.getBalance().compareTo(new BigDecimal("60.00")));
                })
                .verifyComplete();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-2");
        verify(accountsRepo).save(any(Account.class));
        verify(movementsRepo).save(any(Movements.class));
    }

    // ---------- Errores de negocio ----------

    @Test
    void error_cuenta_no_encontrada() {
        when(accountsRepo.findActiveAccountsByNumberAndType("NOPE")).thenReturn(Mono.empty());

        var command = cmd(MovementType.DEPOSIT, new AccountID("NOPE"), AccountType.SAVINGS, new BigDecimal("10"));

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.ACCOUNT_NOT_FOUND,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("NOPE");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }

    @Test
    void error_sobregiro_al_retirar() {
        var acc = activeAccount(new BigDecimal("30.00"), AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-3")).thenReturn(Mono.just(acc));

        var command = cmd(MovementType.WITHDRAW, new AccountID("ACC-1"), AccountType.SAVINGS, new BigDecimal("50.00"));

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.INSUFFICIENT_FUNDS,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-3");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }

    @Test
    void error_monto_invalido_cero_en_deposito() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var acc = activeAccount(new BigDecimal("100.00"), AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-4")).thenReturn(Mono.just(acc));

        var command = cmd(MovementType.DEPOSIT, new AccountID("ACC-1"), AccountType.SAVINGS, BigDecimal.ZERO);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INVALID_AMOUNT,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-4");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_fondos_insuficientes() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var acc = activeAccount(new BigDecimal("0"), AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-4")).thenReturn(Mono.just(acc));

        var command = cmd(MovementType.DEPOSIT, new AccountID("ACC-1"), AccountType.SAVINGS, BigDecimal.ZERO);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INSUFFICIENT_FUNDS,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-4");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_monto_movimiento_nulo() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var acc = activeAccount(new BigDecimal("10.00"), AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-4")).thenReturn(Mono.just(acc));

        var command = cmd(MovementType.DEPOSIT, new AccountID("ACC-1"), AccountType.SAVINGS, null);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INVALID_AMOUNT,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-4");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_cuenta_inactiva() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var acc = inactiveAccount(new BigDecimal("0"), AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-4")).thenReturn(Mono.just(acc));

        var command = cmd(MovementType.DEPOSIT, new AccountID("ACC-1"), AccountType.SAVINGS, BigDecimal.ZERO);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INACTIVE_ACCOUNT,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-4");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_cuenta_activa_null_balance() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var acc = activeAccount(null, AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType("ACC-4")).thenReturn(Mono.just(acc));

        var command = cmd(MovementType.DEPOSIT, new AccountID("ACC-1"), AccountType.SAVINGS, new BigDecimal("10.00"));

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INSUFFICIENT_FUNDS,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType("ACC-4");
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }
}
