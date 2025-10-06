package com.devsu.banking.account_movements.api;

import com.devsu.banking.account_movements.api.handler.MovementsServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class MovementsPersonRouter {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(MovementsServiceHandler serviceHandler) {
        return route(
                POST("/api/movimientos"), serviceHandler::handleRegisterMovementsUseCase)
                //   .andRoute(PUT("/api/customer/{codeId}"), serviceHandler::handleUpdateCustomerUseCase)
                //  .andRoute(GET("/api/customer/{codeId}"), serviceHandler::handleGetCustomerByIdUseCase)
                //.andRoute(DELETE("/api/customer/{codeId}"), serviceHandler::handleRemoveCustomerByIdUseCase)
                ;
    }
}
