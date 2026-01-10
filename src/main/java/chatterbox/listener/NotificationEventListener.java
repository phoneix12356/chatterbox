package chatterbox.listener;

import chatterbox.events.NotificationEvent;
import chatterbox.services.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationServiceImpl notificationServiceImpl;

  @EventListener
  @Async
  public void handleNotificationEvent(NotificationEvent event) {
    notificationServiceImpl.createNotification(event.getNotification());
  }
}
