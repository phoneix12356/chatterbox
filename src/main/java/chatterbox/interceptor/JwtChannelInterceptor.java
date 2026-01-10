package chatterbox.interceptor;

import chatterbox.services.CustomUserDetailsService;
import chatterbox.utils.JwtUtility;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtility jwtUtility;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String email = jwtUtility.extractEmail(token);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (jwtUtility.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        accessor.setUser(authentication);

                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("Missing Authorization header");
            }
        }
        return message;
    }
}