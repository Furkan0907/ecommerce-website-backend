package com.furkan.config;

import com.furkan.handler.AuthEntryPoint;
import com.furkan.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    public static final String BASEURL = "/api/auth";
    public static final String REGISTER = BASEURL +"/register";
    public static final String AUTHENTICATE = BASEURL + "/authenticate";
    public static final String REFRESH_TOKEN = BASEURL + "/refresh-token";
    public static final String LOGOUT = BASEURL + "/logout";
    public static final String EMAIL_EXISTS = BASEURL + "/check-email";
    public static final String RESET_PASSWORD = BASEURL + "/reset-password";
    public static final String STRIPE_WEBHOOK = "/api/payments/webhook";

    public static final String[]  SWAGGER_PATHS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(REGISTER, AUTHENTICATE, REFRESH_TOKEN, LOGOUT, EMAIL_EXISTS, RESET_PASSWORD).permitAll()

                        .requestMatchers(SWAGGER_PATHS).permitAll()

                        .requestMatchers(STRIPE_WEBHOOK).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/search/by-name").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()

                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authEntryPoint))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
