package com.devsu.banking.account_movements.api.handler;

import com.devsu.banking.account_movements.model.cqrs.command.RegisterMovementCommand;
import com.devsu.banking.account_movements.usecase.registermovements.RegisterMovementsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MovementsServiceHandler {

    private final RegisterMovementsUseCase registerMovementsUseCase;

//    private final CreateCustomerUseCase createCustomerUseCase;
//    private final UpdateCustomerUseCase updateCustomerUseCase;
//    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
//    private final RemoveCustomerByIdUseCase removeCustomerByIdUseCase;


    public Mono<ServerResponse> handleRegisterMovementsUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterMovementCommand.class)
                .flatMap(registerMovementsUseCase::handle)
                .flatMap(ServerResponse.ok()::bodyValue);
    }
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
