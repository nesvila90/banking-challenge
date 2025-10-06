package com.devsu.banking.person_customer.api;

import com.devsu.banking.person_customer.api.dto.UpdateCustomerRequestDTO;
import com.devsu.banking.person_customer.model.cqrs.command.CreateCustomerCommand;
import com.devsu.banking.person_customer.model.cqrs.command.RemoveCustomerByIdCommand;
import com.devsu.banking.person_customer.model.cqrs.command.UpdateCustomerCommand;
import com.devsu.banking.person_customer.model.cqrs.query.GetCustomerByIdQuery;
import com.devsu.banking.person_customer.usecase.CreateCustomerUseCase;
import com.devsu.banking.person_customer.usecase.GetCustomerByIdUseCase;
import com.devsu.banking.person_customer.usecase.RemoveCustomerByIdUseCase;
import com.devsu.banking.person_customer.usecase.UpdateCustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerServiceHandler {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    private final RemoveCustomerByIdUseCase removeCustomerByIdUseCase;


    public Mono<ServerResponse> handleCreateCustomerUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateCustomerCommand.class)
                .flatMap(createCustomerUseCase::execute)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleUpdateCustomerUseCase(ServerRequest serverRequest) {
        var codeId = serverRequest.pathVariable("codeId");
        return serverRequest.bodyToMono(UpdateCustomerRequestDTO.class)
                .map(UpdateCustomerRequestDTO::customerData)
                .map(customer -> new UpdateCustomerCommand(customer.getPersonData(), customer, codeId))
                .flatMap(updateCustomerUseCase::execute)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleGetCustomerByIdUseCase(ServerRequest serverRequest) {
        var codeId = serverRequest.pathVariable("codeId");
        return Mono.just(new GetCustomerByIdQuery(codeId))
                .flatMap(getCustomerByIdUseCase::execute)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> handleRemoveCustomerByIdUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RemoveCustomerByIdCommand.class)
                .flatMap(removeCustomerByIdUseCase::execute)
                .then(ServerResponse.noContent().build());
    }

}
