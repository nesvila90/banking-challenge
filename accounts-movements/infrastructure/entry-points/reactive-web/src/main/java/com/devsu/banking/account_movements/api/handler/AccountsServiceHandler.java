package com.devsu.banking.account_movements.api.handler;

import com.devsu.banking.account_movements.model.cqrs.command.ChangeAccountStatusCommand;
import com.devsu.banking.account_movements.model.cqrs.command.CreateAccountCommand;
import com.devsu.banking.account_movements.model.cqrs.query.FetchAccountByCustomerQuery;
import com.devsu.banking.account_movements.model.cqrs.query.FetchAccountDataQuery;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.model.entities.accounts.ids.OwnerId;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.ChangeAccountDataUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.CreateAccountUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.FetchAccountsByUserUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.FetchActiveAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.UUID;

import static com.devsu.banking.account_movements.api.utils.ServerRequestUtils.getPathVariable;
import static com.devsu.banking.account_movements.model.entities.accounts.AccountType.from;

@Component
@RequiredArgsConstructor
public class AccountsServiceHandler {


    private final CreateAccountUseCase createAccountUseCase;
    private final ChangeAccountDataUseCase changeAccountDataUseCase;
    private final FetchActiveAccountUseCase fetchActiveAccountUseCase;
    private final FetchAccountsByUserUseCase fetchAccountsByUserUseCase;

    private static FetchAccountByCustomerQuery buildFetchAccountByCustomerQuery(String customerId) {
        return new FetchAccountByCustomerQuery(new OwnerId(UUID.fromString(customerId)));
    }

    private static FetchAccountDataQuery buildFetchAccountByCustomerQuery(Tuple2<String, String> requestAttributes) {
        return new FetchAccountDataQuery(new AccountID(requestAttributes.getT1(), from(requestAttributes.getT2())));
    }

    public Mono<ServerResponse> handleCreateAccount(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateAccountCommand.class)
                .flatMap(createAccountUseCase::createAccount)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleChangeAccountStatus(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ChangeAccountStatusCommand.class)
                .flatMap(changeAccountDataUseCase::handle)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleFetchAccountData(ServerRequest serverRequest) {
        var accountNumber = getPathVariable(serverRequest, "number", String.class);
        var accountType = getPathVariable(serverRequest, "type", String.class);
        return Mono.zip(accountNumber, accountType)
                .map(AccountsServiceHandler::buildFetchAccountByCustomerQuery)
                .flatMap(fetchActiveAccountUseCase::handle)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleFetchAccountsByCustomer(ServerRequest serverRequest) {
        var customerId = getPathVariable(serverRequest, "customerId", String.class);
        return customerId
                .map(AccountsServiceHandler::buildFetchAccountByCustomerQuery)
                .flatMapMany(fetchAccountsByUserUseCase::handle)
                .timeout(Duration.ofSeconds(10))
                .collectList()
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
