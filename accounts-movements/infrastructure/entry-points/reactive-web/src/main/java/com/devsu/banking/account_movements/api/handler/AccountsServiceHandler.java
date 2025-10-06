package com.devsu.banking.account_movements.api.handler;

import com.devsu.banking.account_movements.model.cqrs.command.ChangeAccountStatusCommand;
import com.devsu.banking.account_movements.model.cqrs.command.CreateAccountCommand;
import com.devsu.banking.account_movements.model.cqrs.query.FetchAccountDataQuery;
import com.devsu.banking.account_movements.model.entities.accounts.AccountType;
import com.devsu.banking.account_movements.model.entities.accounts.ids.AccountID;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.ChangeAccountDataUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.CreateAccountUseCase;
import com.devsu.banking.account_movements.usecase.registermovements.accounts.FetchActiveAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static com.devsu.banking.account_movements.model.entities.accounts.AccountType.from;

@Component
@RequiredArgsConstructor
public class AccountsServiceHandler {


    private final CreateAccountUseCase createAccountUseCase;
    private final ChangeAccountDataUseCase changeAccountDataUseCase;
    private final FetchActiveAccountUseCase fetchActiveAccountUseCase;

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
        var accountNumber = getPathVariable(serverRequest, "number");
        var accountType = getPathVariable(serverRequest, "type");
        return Mono.zip(accountNumber, accountType)
                .map(AccountsServiceHandler::buildFetchAccountDataQuery)
                .flatMap(fetchActiveAccountUseCase::handle)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    private static FetchAccountDataQuery buildFetchAccountDataQuery(Tuple2<String, String> requestAttributes) {
        return new FetchAccountDataQuery(new AccountID(requestAttributes.getT1()), from(requestAttributes.getT2()));
    }


    public static Mono<String> getPathVariable(ServerRequest serverRequest, String paramName) {
        return Mono.just(serverRequest.pathVariable(paramName));
    }

    //    private final CreateCustomerUseCase createCustomerUseCase;
    //    private final UpdateCustomerUseCase updateCustomerUseCase;
    //    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    //    private final RemoveCustomerByIdUseCase removeCustomerByIdUseCase;
    //
    //
    //    public Mono<ServerResponse> handleCreateCustomerUseCase(ServerRequest serverRequest) {
    //        return serverRequest.bodyToMono(CreateCustomerCommand.class)
    //                .flatMap(createCustomerUseCase::execute)
    //                .flatMap(ServerResponse.ok()::bodyValue);
    //    }
    //
    //    public Mono<ServerResponse> handleUpdateCustomerUseCase(ServerRequest serverRequest) {
    //        var codeId = serverRequest.pathVariable("codeId");
    //        return serverRequest.bodyToMono(UpdateCustomerRequestDTO.class)
    //                .map(UpdateCustomerRequestDTO::customerData)
    //                .map(customer -> new UpdateCustomerCommand(customer.getPersonData(), customer, codeId))
    //                .flatMap(updateCustomerUseCase::execute)
    //                .flatMap(ServerResponse.ok()::bodyValue);
    //    }
    //
    //    public Mono<ServerResponse> handleGetCustomerByIdUseCase(ServerRequest serverRequest) {
    //        var codeId = serverRequest.pathVariable("codeId");
    //        return Mono.just(new GetCustomerByIdQuery(codeId))
    //                .flatMap(getCustomerByIdUseCase::execute)
    //                .flatMap(ServerResponse.ok()::bodyValue);
    //    }
    //
    //    public Mono<ServerResponse> handleRemoveCustomerByIdUseCase(ServerRequest serverRequest) {
    //        return serverRequest.bodyToMono(RemoveCustomerByIdCommand.class)
    //                .flatMap(removeCustomerByIdUseCase::execute)
    //                .then(ServerResponse.noContent().build());
    //    }

}
