package com.devsu.banking.person_customer.api;

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
public class CustomerPersonRouter {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CustomerServiceHandler customerServiceHandler) {
        return route(
                POST("/api/customer"), customerServiceHandler::handleCreateCustomerUseCase)
                .andRoute(PUT("/api/customer/{codeId}"), customerServiceHandler::handleUpdateCustomerUseCase)
                .andRoute(GET("/api/customer/{codeId}"), customerServiceHandler::handleGetCustomerByIdUseCase)
                .andRoute(DELETE("/api/customer/{codeId}"), customerServiceHandler::handleRemoveCustomerByIdUseCase);
    }
}
