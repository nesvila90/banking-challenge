package com.devsu.banking.account_movements.api.utils;

import com.devsu.banking.account_movements.model.commons.exceptions.TechnicalException;
import com.devsu.banking.account_movements.model.commons.exceptions.messages.TechnicalErrorMessage;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ServerRequestUtils {

    private ServerRequestUtils() {
    }

    public static <T> Mono<T> getPathVariable(ServerRequest serverRequest, String paramName, Class<T> clazz) {
        return Mono.just(serverRequest.pathVariable(paramName)).map(clazz::cast);
    }

    public static <T> Mono<T> getRequestVariable(ServerRequest serverRequest, String paramName, Class<T> clazz) {
        return Mono.just(serverRequest.queryParam(paramName)
                        .orElseThrow(() -> new TechnicalException(TechnicalErrorMessage.BAD_REQUEST)))
                .map(clazz::cast);
    }

    public static <T> Mono<T> getDateRequestVariable(ServerRequest serverRequest, String paramName, Class<T> clazz) {
        return Mono.just(serverRequest.queryParam(paramName)
                        .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                        .orElseThrow(() -> new TechnicalException(TechnicalErrorMessage.BAD_REQUEST)))
                .map(clazz::cast);
    }
}
