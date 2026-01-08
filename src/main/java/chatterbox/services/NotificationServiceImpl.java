package chatterbox.services;

import chatterbox.entities.Notification;
import chatterbox.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final chatterbox.repository.UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void createNotification(Notification notification) {
    Notification savedNotification = notificationRepository.save(notification);

    chatterbox.entities.Users receiver = userRepository.findById(notification.getReceiverId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    String email = receiver.getEmail();
    if (email != null) {
      messagingTemplate.convertAndSendToUser(email, "/notification", savedNotification);
    }
  }

  @Override
  public List<Notification> getAllNotification(Long userId) {
    return notificationRepository.findByReceiverId(userId);
  }
}
