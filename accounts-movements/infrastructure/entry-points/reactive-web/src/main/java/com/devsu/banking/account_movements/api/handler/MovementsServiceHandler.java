package com.devsu.banking.account_movements.api.handler;

import com.devsu.banking.account_movements.model.cqrs.command.ChangeMovementCommand;
import com.devsu.banking.account_movements.model.cqrs.command.RegisterMovementCommand;
import com.devsu.banking.account_movements.model.cqrs.query.list.FetchMovementsByAccountQuery;
import com.devsu.banking.account_movements.model.cqrs.query.list.FetchOwnerMovementsGroupedByAccounts;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import com.devsu.banking.account_movements.usecase.registermovements.movements.ChangeMovementUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.movements.FetchMovementsByAccountUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.movements.FetchOwnerMovementsGrouperByAccountsIdUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.movements.RegisterMovementsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.LocalDate;
import java.util.UUID;

import static com.devsu.banking.account_movements.api.utils.ServerRequestUtils.getPathVariable;
import static java.util.UUID.fromString;

@Component
@RequiredArgsConstructor
public class MovementsServiceHandler {

    private final RegisterMovementsUseCase registerMovementsUseCase;
    private final ChangeMovementUseCase changeMovementUseCase;
    private final FetchMovementsByAccountUseCase fetchMovementsByAccountUseCase;
    private final FetchOwnerMovementsGrouperByAccountsIdUseCase fetchOwnerMovementsGrouperByAccountsIdUseCase;

    public Mono<ServerResponse> handleRegisterMovementsUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterMovementCommand.class)
                .flatMap(registerMovementsUseCase::handle)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleChangeMovementsDataUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ChangeMovementCommand.class)
                .flatMap(changeMovementUseCase::handle)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleFetchMovementsByAccount(ServerRequest serverRequest) {
        var accountNumber = getPathVariable(serverRequest, "number", String.class);
        var accountType = getPathVariable(serverRequest, "type", String.class);
        return Mono.zip(accountNumber, accountType)
                .map(MovementsServiceHandler::mapQuery)
                .flatMapMany(fetchMovementsByAccountUseCase::handle)
                .collectList()
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleFetchMovementsGroupedByAccountsByOwner(ServerRequest serverRequest) {
        var accountNumber = getPathVariable(serverRequest, "clienteId", String.class);
        var fromDate = getPathVariable(serverRequest, "desde", LocalDate.class);
        var untilDate = getPathVariable(serverRequest, "hasta", LocalDate.class);
        return Mono.zip(accountNumber, fromDate, untilDate)
                .map(MovementsServiceHandler::mapQuery)
                .flatMapMany(fetchOwnerMovementsGrouperByAccountsIdUseCase::handle)
                .collectList()
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    private static FetchOwnerMovementsGroupedByAccounts mapQuery(Tuple3<String, LocalDate, LocalDate> objects) {
        return new FetchOwnerMovementsGroupedByAccounts(new OwnerId(fromString(objects.getT1())), objects.getT2(), objects.getT3());
    }

    private static FetchMovementsByAccountQuery mapQuery(Tuple2<String, String> objects) {
        return new FetchMovementsByAccountQuery(new AccountID(objects.getT1(), AccountType.from(objects.getT2())));
    }
}
