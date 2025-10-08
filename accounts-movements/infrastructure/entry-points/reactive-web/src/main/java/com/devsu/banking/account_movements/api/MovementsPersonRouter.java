package com.devsu.banking.account_movements.api;

import com.devsu.banking.account_movements.api.handler.MovementsServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class MovementsPersonRouter {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(MovementsServiceHandler serviceHandler) {
        return route(
                POST("/api/movimientos"), serviceHandler::handleRegisterMovementsUseCase)
                .andRoute(PUT("/api/movimientos"), serviceHandler::handleChangeMovementsDataUseCase)
                .andRoute(GET("/api/movimientos/reportes"), serviceHandler::handleFetchMovementsGroupedByAccountsByOwner)
                .andRoute(GET("/api/movimientos/{codeId}"), serviceHandler::handleFetchMovementsByAccount)

                ;
    }
}
