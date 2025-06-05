package ptzt.f1Hub.config.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ptzt.f1Hub.exceptions.InvalidTokenException;

import java.io.IOException;
import java.util.Collections;

@Configuration
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailService appUserDetailService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return (path.contains("/auth") && !(path.contains("/auth/check")) ||
                path.contains("/swagger") ||
                path.contains("/v3/api-docs") ||
                path.contains("/h2-console"));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (CorsUtils.isPreFlightRequest(request)) {
            UsernamePasswordAuthenticationToken fakeAuthentication = new UsernamePasswordAuthenticationToken(
                    "anonymousUser",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"
                    )));


            SecurityContextHolder.getContext().setAuthentication(fakeAuthentication);
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = appUserDetailService.loadUserByUsername(username);

                if (userDetails != null && jwtService.isValidToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, userDetails.getPassword(), userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);

                } else {
                    InvalidTokenException invalidTokenException = new InvalidTokenException("El token proporcionado no es válido o a expirado");
                    handlerExceptionResolver.resolveException(request, response, null, invalidTokenException);
                }
            }
        } catch (JwtException ex) {
            InvalidTokenException invalidTokenException = new InvalidTokenException("El token proporcionado no es válido o a expirado");
            handlerExceptionResolver.resolveException(request, response, null, invalidTokenException);

        }
    }
}