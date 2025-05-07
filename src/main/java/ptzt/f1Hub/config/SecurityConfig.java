package ptzt.f1Hub.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.servlet.HandlerExceptionResolver;
import ptzt.f1Hub.config.security.JwtAuthFilter;
import ptzt.f1Hub.domain.exceptions.UserUnauthorizedException;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                        registry.requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/swagger/**").permitAll()

                                //AUTH
                                .requestMatchers("/auth/**").permitAll()

                                //APPUSERS
                                .requestMatchers(HttpMethod.PUT, "/appUsers/**").authenticated()

                                //DRIVERS
                                .requestMatchers("/drivers/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/drivers/points/*").hasRole("MANTAINER")

                                //TEAMS
                                .requestMatchers("/teams/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/teams/points/*").hasRole("MANTAINER")

                                //OFFERS
                                .requestMatchers("/offers/**").authenticated()

                                //LEAGUES
                                .requestMatchers("/leagues/**").authenticated()

                                //MARKETITEMS
                                .requestMatchers("/marketItems/**").authenticated()

                                //GET
                                .requestMatchers(HttpMethod.GET).permitAll()

                                .anyRequest().hasRole("ADMIN")
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(this::configureExceptionHandling)
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .build();
    }

    private void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling) {
        exceptionHandling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    UserUnauthorizedException invalidTokenException = new UserUnauthorizedException("Denied access: authority not enought");
                    handlerExceptionResolver.resolveException(request, response, null, invalidTokenException);
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    UserUnauthorizedException invalidTokenException = new UserUnauthorizedException("Denied access: invalid credentials");
                    handlerExceptionResolver.resolveException(request, response, null, invalidTokenException);
                });
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
