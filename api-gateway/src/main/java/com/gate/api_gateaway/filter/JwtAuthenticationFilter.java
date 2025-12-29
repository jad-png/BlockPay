package com.gate.api_gateaway.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
abstract class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    public JwtAuthenticationFilter() {
        super(Config.class);
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

            // TODO: verfiication b config.getSecret();

            return chain.filter(exchange);
        };
    }
}
