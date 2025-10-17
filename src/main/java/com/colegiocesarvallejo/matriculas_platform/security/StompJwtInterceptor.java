package com.colegiocesarvallejo.matriculas_platform.security;

import com.colegiocesarvallejo.matriculas_platform.service.usuario.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompJwtInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else if (accessor.getSessionAttributes() != null) {
                Object tokenAttr = accessor.getSessionAttributes().get("token");
                if (tokenAttr != null) {
                    token = tokenAttr.toString();
                    log.debug("🔸 Token recuperado desde handshake attributes");
                }
            }

            if (token != null) {
                try {
                    String username = jwtUtil.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);

                        log.info("✅ Usuario autenticado por STOMP: {}", username);
                    } else {
                        log.warn("❌ Token JWT inválido en conexión STOMP");
                    }
                } catch (Exception e) {
                    log.error("Error validando JWT en STOMP: {}", e.getMessage());
                }
            } else {
                log.warn("⚠️ No se encontró token JWT ni en header ni en handshake attributes");
            }
        }

        return message;
    }
}
