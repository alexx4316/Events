// com.Events.Tickets.infraestructura.security.jwt.JwtAuthFilter

package com.Events.Tickets.infraestructura.security.jwt;

import com.Events.Tickets.infraestructura.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Verificar si hay un token "Bearer" en el header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Continúa al siguiente filtro o al endpoint
        }

        // 2. Extraer el JWT (los 7 caracteres de "Bearer " + espacio)
        jwt = authHeader.substring(7);

        // 3. Extraer el username del token
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Manejar token inválido o expirado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o expirado.");
            return;
        }

        // 4. Si el username es válido y NO hay un usuario autenticado en el contexto de Spring
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Cargar los detalles del usuario (esto usa tu Dominio)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Validar el token con los detalles cargados
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Establecer la autenticación en el Contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con la cadena de filtros de Spring Security
        filterChain.doFilter(request, response);
    }
}