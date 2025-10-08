package com.devsu.banking.account_movements.usecase.registermovements.movements;

import com.devsu.banking.account_movements.model.cqrs.command.ChangeMovementCommand;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.MovementId;
import com.devsu.banking.account_movements.model.entities.movements.MovementType;
import com.devsu.banking.account_movements.model.entities.movements.Movements;
import com.devsu.banking.account_movements.model.entities.movements.gateways.MovementsRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ChangeMovementUseCaseTest {

    @Mock
    private MovementsRepositoryGateway repository;

    @InjectMocks
    private ChangeMovementUseCase useCase;

    @Captor
    private ArgumentCaptor<Movements> movementsCaptor;

    private String movementUUID;
    private ChangeMovementCommand command;

    @BeforeEach
    void setUp() {
        movementUUID = UUID.randomUUID().toString();
        // Asumiendo que ChangeMovementCommand es un record con: (MovementId movementId, MovementType type, BigDecimal amount, String accountNumber)
        command = new ChangeMovementCommand(
                new MovementId(movementUUID),
                MovementType.DEPOSIT,
                new AccountID("ACC-12345", AccountType.SAVINGS),
                AccountType.SAVINGS,
                new BigDecimal("150.50")
        );
    }

    @Test
    void shouldUpdateMovementUsingMappedFields() {
        // arrange
        Movements updated = new Movements();
        updated.setId(movementUUID);
        updated.setType(MovementType.DEPOSIT);
        updated.setAmount(new BigDecimal("150.50"));
        AccountID accountID = new AccountID("ACC-12345", AccountType.SAVINGS);
        updated.setAccountSource(accountID);
        when(repository.updateMovement(any(Movements.class))).thenReturn(Mono.just(updated));

        // act & assert
        StepVerifier.create(useCase.handle(command))
                .expectNextMatches(result ->
                        movementUUID.equals(result.getId()) &&
                                MovementType.DEPOSIT.equals(result.getType()) &&
                                new BigDecimal("150.50").compareTo(result.getAmount()) == 0 &&
                                "ACC-12345".equals(result.getAccountSource().id()))
                .verifyComplete();

        // verify mapping exacto
        verify(repository).updateMovement(movementsCaptor.capture());
        Movements mapped = movementsCaptor.getValue();
        assertThat(mapped.getId()).isEqualTo(movementUUID);
        assertThat(mapped.getType()).isEqualTo(MovementType.DEPOSIT);
        assertThat(mapped.getAmount()).isEqualByComparingTo("150.50");
        assertThat(mapped.getAccountSource()).isEqualTo(accountID);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldPropagateRepositoryError() {
        // arrange
        RuntimeException boom = new RuntimeException("DB down");
        when(repository.updateMovement(any(Movements.class))).thenReturn(Mono.error(boom));

        // act & assert
        StepVerifier.create(useCase.handle(command))
                .expectErrorMatches(ex -> ex == boom || "DB down".equals(ex.getMessage()))
                .verify();

        verify(repository).updateMovement(any(Movements.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldMapNegativeAmountsForWithdrawals() {
        // arrange
        ChangeMovementCommand withdrawCmd = new ChangeMovementCommand(
                new MovementId(movementUUID),
                MovementType.WITHDRAW,
                new AccountID("ACC-999", AccountType.SAVINGS),
                AccountType.SAVINGS,
                new BigDecimal("-200.00")

        );

        Movements updated = new Movements();
        updated.setId(movementUUID);
        updated.setType(MovementType.WITHDRAW);
        updated.setAmount(new BigDecimal("-200.00"));
        updated.setAccountSource(new AccountID("ACC-999", AccountType.SAVINGS));

        when(repository.updateMovement(any(Movements.class))).thenReturn(Mono.just(updated));

        // act & assert
        StepVerifier.create(useCase.handle(withdrawCmd))
                .expectNextMatches(m ->
                        m.getId().equals(movementUUID) &&
                                m.getType() == MovementType.WITHDRAW &&
                                m.getAmount().compareTo(new BigDecimal("-200.00")) == 0 &&
                                "ACC-999".equals(m.getAccountSource().id()))
                .verifyComplete();

        verify(repository).updateMovement(movementsCaptor.capture());
        Movements mapped = movementsCaptor.getValue();
        assertThat(mapped.getType()).isEqualTo(MovementType.WITHDRAW);
        assertThat(mapped.getAmount()).isEqualByComparingTo("-200.00");
        assertThat(mapped.getAccountSource().id()).isEqualTo("ACC-999");
        verifyNoMoreInteractions(repository);
    }

}

