package chatterbox.listener;

import chatterbox.dtos.response.StatusUpdateDto;
import chatterbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j

public class WebSocketEventsListener {

  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @EventListener
  @Transactional
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    Principal user = event.getUser();
    if (user != null) {
      String email = user.getName();
      log.info("User connected: {}", email);
      updateUserStatus(email, true);
    }
  }

  @EventListener
  @Transactional
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    Principal user = headerAccessor.getUser();

    if (user != null) {
      String email = user.getName();
      log.info("User disconnected: {}", email);
      updateUserStatus(email, false);
    }
  }

  private void updateUserStatus(String email, boolean isOnline) {
    userRepository.findByEmail(email).ifPresent(user -> {
      user.setOnline(isOnline);
      userRepository.save(user);

      StatusUpdateDto statusUpdate = StatusUpdateDto.builder()
          .email(email)
          .status(isOnline ? "ONLINE" : "OFFLINE")
          .build();

      messagingTemplate.convertAndSend("/topic/presence", statusUpdate);
      log.info("Broadcasted status update for {}: {}", email, statusUpdate.getStatus());
    });
  }

  @EventListener(ApplicationReadyEvent.class)
  public void resetUserStatus() {
    log.info("Resetting all users to OFFLINE on startup...");
    // This might be slow for millions of users, but suitable for this scale.
    // Direct SQL update would be faster.
    // userRepository.updateAllUsersToOffline(); // Ideal custom query
    // For now, doing it via a simpler approach if repository allows, or just leave
    // it simpler.
    // Actually, let's use a custom query in UserRepository to be efficient.
    userRepository.updateAllUsersToOffline();
  }
}
