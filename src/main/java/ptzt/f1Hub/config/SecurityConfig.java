package ptzt.f1Hub.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
import ptzt.f1Hub.exceptions.UserUnauthorizedException;


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
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                        registry.requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/favicon.ico",
                                        "/auth/**",
                                        "/h2-console/**"
                                ).permitAll()

                                //AUTH
                                .requestMatchers("/auth/check").hasAnyRole("ADMIN","REVIEWER","PLAYER")
                                .requestMatchers("/auth/**").permitAll()

                                //ACCOUNTS
                                .requestMatchers(HttpMethod.PUT, "/accounts/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/accounts/**").hasRole("ADMIN")

                                //APPUSERS
                                .requestMatchers(HttpMethod.PUT, "/appUsers/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/appUsers/**").hasAnyRole("ADMIN","REVIEWER","PLAYER")
                                .requestMatchers(HttpMethod.POST, "/appUsers/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/appUsers/**").hasRole("ADMIN")

                                //DRIVERS
                                .requestMatchers(HttpMethod.PUT, "/drivers/points/*").hasRole("REVIEWER")
                                .requestMatchers("/drivers/**").hasRole("ADMIN")

                                //TEAMS
                                .requestMatchers(HttpMethod.PUT, "/teams/points/*").hasRole("REVIEWER")
                                .requestMatchers("/teams/**").hasRole("ADMIN")

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
