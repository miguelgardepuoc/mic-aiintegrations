package com.antharos.aiintegrations.boot.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/problem+json");
                          response
                              .getWriter()
                              .write(
                                  """
                                                                  {
                                                                    "type": "authentication-error",
                                                                    "title": "Unauthorized",
                                                                    "status": 401,
                                                                    "detail": "Authentication is required to access this resource",
                                                                    "instance": "%s"
                                                                  }
                                                                  """
                                      .formatted(request.getRequestURI()));
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/problem+json");
                          response
                              .getWriter()
                              .write(
                                  """
                                                                  {
                                                                    "type": "access-denied",
                                                                    "title": "Forbidden",
                                                                    "status": 403,
                                                                    "detail": "You do not have permission to access this resource",
                                                                    "instance": "%s"
                                                                  }
                                                                  """
                                      .formatted(request.getRequestURI()));
                        }))
        .build();
  }
}
