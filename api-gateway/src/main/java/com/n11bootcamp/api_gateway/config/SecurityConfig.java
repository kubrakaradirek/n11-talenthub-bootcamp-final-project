package com.n11bootcamp.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity //GATEWAY ŞARTI
public class SecurityConfig {

    // CORS AYARI: React (Vite) projesine kapıyı açan anahtar
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Vite varsayılan portu 5173
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Buradaki adresin Keycloak adresinle birebir aynı olduğundan emin ol
        return ReactiveJwtDecoders.fromIssuerLocation("http://localhost:8081/realms/microservice-realm");
    }
    // GÜVENLİK FİLTRESİ: Gateway üzerinden geçen trafiği yönetir
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // API'ler için CSRF'i kapandı
                .authorizeExchange(exchanges -> exchanges
                        // Kurallar sırasıyla
                        .pathMatchers("/eureka/**").permitAll() // Eureka paneli açık
                        .pathMatchers("/api/user/signup", "/api/user/signin").permitAll() // Kayıt ve Giriş açık
                        .pathMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Ürün listeleme HERKESE açık

                        // En sona "diğer her şey için giriş yap" kuralı tanımlanır.
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}