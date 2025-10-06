package com.devsu.banking.account_movements.api;

import com.devsu.banking.account_movements.api.handler.AccountsServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AccountsRouter {
    @Bean
    public RouterFunction<ServerResponse> routerAccountsFunction(AccountsServiceHandler accountsServiceHandler) {
        return route(
                POST("/api/cuentas"), accountsServiceHandler::handleCreateAccount)
                //.andRoute(PUT("/api/customer/{codeId}"), accountsServiceHandler::handleUpdateCustomerUseCase)
                //.andRoute(GET("/api/customer/{codeId}"), accountsServiceHandler::handleGetCustomerByIdUseCase)
                //.andRoute(DELETE("/api/customer/{codeId}"), accountsServiceHandler::handleRemoveCustomerByIdUseCase);
        ;
    }
}
