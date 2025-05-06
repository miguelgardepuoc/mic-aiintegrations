package com.antharos.aiintegrations.boot.config;

import com.antharos.aiintegrations.infrastructure.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);

    try {
      if (!this.jwtService.isTokenValid(jwt)) {
        filterChain.doFilter(request, response);
        return;
      }

      final String username = this.jwtService.extractUsername(jwt);
      List<GrantedAuthority> authorities = this.jwtService.extractRoles(jwt);

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(username, null, authorities);
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authToken);
      SecurityContextHolder.getContext().getAuthentication();

    } catch (Exception exception) {
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }
}
