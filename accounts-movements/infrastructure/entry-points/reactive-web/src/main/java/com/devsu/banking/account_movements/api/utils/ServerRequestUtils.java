package com.devsu.banking.account_movements.api.utils;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public final class ServerRequestUtils {

    private ServerRequestUtils() {
    }

    public static <T> Mono<T> getPathVariable(ServerRequest serverRequest, String paramName, Class<T> clazz) {
        return Mono.just(serverRequest.pathVariable(paramName)).map(clazz::cast);
    }
}
