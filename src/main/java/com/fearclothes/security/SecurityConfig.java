package com.fearclothes.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        antMatcher("/"),
                        antMatcher("/publicacion/*"),
                        antMatcher("/login"),
                        antMatcher("/registro"),
                        antMatcher("/css/**"),
                        antMatcher("/webjars/**"),
                        antMatcher("/img/**"),
                        antMatcher("/uploads/**"),
                        antMatcher("/error"),
                        antMatcher("/h2-console/**")).permitAll()
                // "/publicacion/*" solo cubre /publicacion/{id}; editar, eliminar, pujar, etc. exigen sesión
                .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
                .requestMatchers(antMatcher("/vender")).hasRole("VENDEDOR")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/"))
            // La consola H2 (solo desarrollo) necesita estas dos excepciones
            .csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher("/h2-console/**")))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
