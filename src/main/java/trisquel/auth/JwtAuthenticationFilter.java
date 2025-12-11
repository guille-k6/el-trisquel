package trisquel.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            // Extraer el token del header
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                try {
                    username = jwtUtil.extractUsername(jwt);
                } catch (ExpiredJwtException e) {
                    logger.warn("Token expirado para la petición: {} {}", request.getMethod(), request.getRequestURI());
                } catch (MalformedJwtException e) {
                    logger.warn("Token JWT malformado en la petición: {} {}", request.getMethod(), request.getRequestURI());
                } catch (Exception e) {
                    logger.error("Error al extraer username del token: {}", e.getMessage());
                }
            }

            // Validar el token y establecer la autenticación
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        logger.debug("Usuario autenticado: {} para {} {}", username, request.getMethod(), request.getRequestURI());
                    } else {
                        logger.warn("Token inválido para usuario: {}", username);
                    }
                } catch (UsernameNotFoundException e) {
                    logger.warn("Usuario no encontrado: {}", username);
                } catch (Exception e) {
                    logger.error("Error al validar token para usuario {}: {}", username, e.getMessage());
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Error inesperado en JwtAuthenticationFilter: {}", e.getMessage(), e);

            // Limpiar el contexto de seguridad
            SecurityContextHolder.clearContext();

            // Enviar respuesta de error si no se envió aún
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Error de autenticación\"}");
            }
        }
    }
}
