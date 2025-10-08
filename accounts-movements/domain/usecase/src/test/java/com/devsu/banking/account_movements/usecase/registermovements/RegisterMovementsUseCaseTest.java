package com.devsu.banking.account_movements.usecase.registermovements;

import com.devsu.banking.account_movements.model.commons.exceptions.BusinessException;
import com.devsu.banking.account_movements.model.commons.exceptions.messages.BusinessErrorMessage;
import com.devsu.banking.account_movements.model.entities.accounts.Account;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.gateways.AccountsRepositoryGateway;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import com.devsu.banking.account_movements.usecase.registermovements.movements.RegisterMovementsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static com.devsu.banking.account_movements.model.entities.movements.MovementType.DEPOSIT;
import static com.devsu.banking.account_movements.usecase.registermovements.factory.MockFactory.buildActiveSavingsAccountSnapshot;
import static com.devsu.banking.account_movements.usecase.registermovements.factory.MockFactory.buildInactiveSavingsAccountSnapshot;
import static com.devsu.banking.account_movements.usecase.registermovements.factory.MockFactory.buildMovement;
import static com.devsu.banking.account_movements.usecase.registermovements.factory.MockFactory.cmd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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


    @Test
    void deposit_ok_persiste_movimiento_y_cuenta() {
        // Given
        var id = "ACC-1";
        var initial = new BigDecimal("100.00");
        var amount = new BigDecimal("50.00");
        var balance = new BigDecimal("100.00");
        var acc = buildActiveSavingsAccountSnapshot(id, initial, balance);
        var accountNumber = new AccountID(id, AccountType.SAVINGS);
        var movementData = buildMovement(id, amount, balance.add(amount), accountNumber, MovementType.DEPOSIT);

        var accUpdated = buildActiveSavingsAccountSnapshot(id, initial, balance.add(amount));
        var command = cmd(DEPOSIT, accountNumber, AccountType.SAVINGS, amount);

        when(accountsRepo.findActiveAccountsByNumberAndType(any(AccountID.class))).thenReturn(acc);
        when(accountsRepo.updateAccount(any(Account.class))).thenReturn(accUpdated);
        when(movementsRepo.save(any(Movements.class))).thenReturn(Mono.just(movementData));

        // When / Then
        StepVerifier.create(useCase.handle(command))
                .assertNext(saved -> {
                    assertEquals(DEPOSIT, saved.getType());
                    assertEquals(0, saved.getAmount().compareTo(amount));
                    assertEquals(0, saved.getBalance().compareTo(new BigDecimal("150.00")));
                    assertNotNull(saved.getDate());
                })
                .verifyComplete();

        verify(accountsRepo, times(1)).updateAccount(any(Account.class));
        verify(movementsRepo, times(1)).save(any(Movements.class));
        verify(accountsRepo, times(1)).findActiveAccountsByNumberAndType(any(AccountID.class));
        verifyNoMoreInteractions(accountsRepo, movementsRepo);
    }

    @Test
    void withdraw_ok_con_fondos_suficientes() {
        var id = "ACC-2";

        var amount = new BigDecimal("40.00");
        var initial = BigDecimal.ZERO;
        var balance = new BigDecimal("100.00");
        var acc = buildActiveSavingsAccountSnapshot(id, initial, balance);
        var accountWithNoFunds = new AccountID(id, AccountType.SAVINGS);
        var command = cmd(MovementType.WITHDRAW, accountWithNoFunds, AccountType.SAVINGS, amount);


        var accountNumber = new AccountID(id, AccountType.SAVINGS);

        var movementData = buildMovement(id, amount, balance.subtract(amount), accountNumber, MovementType.WITHDRAW);
        var accUpdated = buildActiveSavingsAccountSnapshot(id, initial, balance);

        when(accountsRepo.findActiveAccountsByNumberAndType(any(AccountID.class))).thenReturn(acc);
        when(accountsRepo.updateAccount(any(Account.class))).thenReturn(accUpdated);
        when(movementsRepo.save(any(Movements.class))).thenReturn(Mono.just(movementData));

        StepVerifier.create(useCase.handle(command))
                .assertNext(saved -> {
                    assertEquals(MovementType.WITHDRAW, saved.getType());
                    assertEquals(0, saved.getAmount().compareTo(new BigDecimal("40.00")));
                    assertEquals(0, saved.getBalance().compareTo(new BigDecimal("60.00")));
                })
                .verifyComplete();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountWithNoFunds);
        verify(accountsRepo).updateAccount(any(Account.class));
        verify(movementsRepo).save(any(Movements.class));
    }

    // ---------- Errores de negocio ----------

    @Test
    void error_cuenta_no_encontrada() {
        var accountNoNumber = new AccountID("NOPE", AccountType.SAVINGS);
        when(accountsRepo.findActiveAccountsByNumberAndType(accountNoNumber)).thenReturn(Mono.empty());

        var command = cmd(DEPOSIT, accountNoNumber, AccountType.SAVINGS, new BigDecimal("10"));
        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.ACCOUNT_NOT_FOUND,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountNoNumber);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }

    @Test
    void error_sobregiro_al_retirar() {
        var id = "ACC-1";
        var acc = buildActiveSavingsAccountSnapshot(id, BigDecimal.ZERO, new BigDecimal("25.0"));
        var accountOverdraft = new AccountID(id, AccountType.CHECKING);
        when(accountsRepo.findActiveAccountsByNumberAndType(accountOverdraft)).thenReturn(acc);

        var command = cmd(MovementType.WITHDRAW, accountOverdraft, AccountType.SAVINGS, new BigDecimal("50.00"));

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INSUFFICIENT_FUNDS,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountOverdraft);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }

    @Test
    void error_monto_invalido_cero_en_deposito() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var id = "ACC-4";
        var acc = buildActiveSavingsAccountSnapshot(id, BigDecimal.ZERO, new BigDecimal("10.0"));
        var accountWithFunds = new AccountID(id, AccountType.SAVINGS);

        when(accountsRepo.findActiveAccountsByNumberAndType(accountWithFunds)).thenReturn(acc);

        var command = cmd(DEPOSIT, accountWithFunds, AccountType.SAVINGS, BigDecimal.ZERO);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INVALID_AMOUNT,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountWithFunds);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_fondos_insuficientes() {
        var id = "ACC-5";
        var acc = buildActiveSavingsAccountSnapshot(id, BigDecimal.ZERO, BigDecimal.ZERO);
        var accountWithFunds = new AccountID(id, AccountType.SAVINGS);
        var command = cmd(DEPOSIT, accountWithFunds, AccountType.SAVINGS, BigDecimal.ZERO);

        when(accountsRepo.findActiveAccountsByNumberAndType(accountWithFunds)).thenReturn(acc);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INSUFFICIENT_FUNDS,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountWithFunds);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_monto_movimiento_nulo() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)
        var id = "ACC-6";
        var acc = buildActiveSavingsAccountSnapshot(id, BigDecimal.ZERO, new BigDecimal("10.0"));
        var accountWithFunds = new AccountID(id, AccountType.SAVINGS);
        var command = cmd(DEPOSIT, accountWithFunds, AccountType.SAVINGS, null);

        when(accountsRepo.findActiveAccountsByNumberAndType(accountWithFunds)).thenReturn(acc);

        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INVALID_AMOUNT,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountWithFunds);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_cuenta_inactiva() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)

        var id = "ACC-7";
        var acc = buildInactiveSavingsAccountSnapshot(id, BigDecimal.ZERO, new BigDecimal("10.0"));
        var accountWithFunds = new AccountID(id, AccountType.SAVINGS);
        var command = cmd(DEPOSIT, accountWithFunds, AccountType.SAVINGS, BigDecimal.ZERO);

        when(accountsRepo.findActiveAccountsByNumberAndType(accountWithFunds)).thenReturn(acc);
        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INACTIVE_ACCOUNT,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountWithFunds);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }


    @Test
    void error_cuenta_activa_null_balance() {
        // Monto 0 -> INVALID_AMOUNT (si la cuenta está activa y con saldo no negativo)

        var id = "ACC-7";
        var acc = buildActiveSavingsAccountSnapshot(id, BigDecimal.ZERO, null);
        var accountWithFunds = new AccountID(id, AccountType.SAVINGS);
        var command = cmd(DEPOSIT, accountWithFunds, AccountType.SAVINGS, new BigDecimal("10.00"));

        when(accountsRepo.findActiveAccountsByNumberAndType(accountWithFunds)).thenReturn(acc);
        StepVerifier.create(useCase.handle(command))
                .expectErrorSatisfies(err -> {
                    assertInstanceOf(BusinessException.class, err);
                    assertEquals(BusinessErrorMessage.INSUFFICIENT_FUNDS,
                            ((BusinessException) err).getBusinessErrorMessage());
                })
                .verify();

        verify(accountsRepo).findActiveAccountsByNumberAndType(accountWithFunds);
        verify(movementsRepo, never()).save(any());
        verify(accountsRepo, never()).save(any());
    }
}
