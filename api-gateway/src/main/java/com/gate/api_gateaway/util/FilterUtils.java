package com.gate.api_gateaway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gate.api_gateaway.dto.ErrorResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class FilterUtils {
    private final ObjectMapper objectMapper;

    public FilterUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<Void> handleException(ServerWebExchange exchange, ErrorResponse errorResponse) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        try {
            byte[] bytes = new ObjectMapper()
                    .findAndRegisterModules()
                    .writeValueAsBytes(errorResponse);

            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }
}
