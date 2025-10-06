package com.devsu.banking.account_movements.api;

import com.devsu.banking.account_movements.api.handler.AccountsServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AccountsRouter {
    @Bean
    public RouterFunction<ServerResponse> routerAccountsFunction(AccountsServiceHandler accountsServiceHandler) {
        return route(
                POST("/api/cuentas"), accountsServiceHandler::handleCreateAccount)
                .andRoute(PATCH("/api/cuentas"), accountsServiceHandler::handleChangeAccountStatus)
                .andRoute(GET("/api/cuentas/{number}/type/{type}"), accountsServiceHandler::handleFetchAccountData)
                .andRoute(GET("/api/cuentas/customer/{customerId}"), accountsServiceHandler::handleFetchAccountsByCustomer)
        ;
    }
}
