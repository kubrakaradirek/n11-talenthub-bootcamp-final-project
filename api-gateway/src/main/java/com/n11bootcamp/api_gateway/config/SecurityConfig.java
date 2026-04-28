package com.n11bootcamp.api_gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//Bu sınıf, hangi isteklere izin verileceğini, hangilerinde token isteneceğini belirleyecektir.
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/eureka/**").permitAll() // Eureka paneli açık
                        .requestMatchers("/api/user/signup", "/api/user/signin").permitAll() // Login ve kayıt açık
                        .anyRequest().authenticated() // Diğer tüm istekler Keycloak Token'ı isteyecek
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}

