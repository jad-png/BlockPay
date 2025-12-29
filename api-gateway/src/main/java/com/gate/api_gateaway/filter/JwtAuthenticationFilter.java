package com.gate.api_gateaway.filter;

import com.gate.api_gateaway.util.JwtUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
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
                throw new RuntimeException("Missing Authorization Header");
            }

            String authHeader = request.getHeaders().get("Authorization").get(0);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid Authorization Header");
            }

            String token =  authHeader.substring(7);

            boolean isValid = jwtUtil.validateToken(token);

            if (!isValid) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }
}
