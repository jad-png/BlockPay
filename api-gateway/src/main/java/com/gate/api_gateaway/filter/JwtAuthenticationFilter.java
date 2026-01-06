package com.gate.api_gateaway.filter;

import com.gate.api_gateaway.dto.ErrorResponse;
import com.gate.api_gateaway.util.FilterUtils;
import com.gate.api_gateaway.util.JwtUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private final JwtUtil jwtUtil;
    private final FilterUtils filterUtils;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, FilterUtils filterUtils) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.filterUtils = filterUtils;
    }

    @Getter
    @Setter
    public static class Config {
        private String secret;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            var request = exchange.getRequest();

            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get("Authorization").get(0);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token =  authHeader.substring(7);

            boolean isValid = jwtUtil.validateToken(token);

            if (!isValid) {
                return onError(exchange, "Token is invalid or expired", HttpStatus.UNAUTHORIZED);
            }

            String userId = jwtUtil.extractUserId(token);

            var modifiedReq = exchange.getRequest().mutate().header("X-USER-ID", userId).build();

            return chain.filter(exchange.mutate().request(modifiedReq).build());
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(err)
                .path(exchange.getRequest().getPath().toString())
                .build();
        return filterUtils.handleException(exchange, error);
    }
}
