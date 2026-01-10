package chatterbox.services;

import chatterbox.entities.Notification;

import java.util.List;

public interface NotificationService {
    void createNotification(Notification notification);
    List<Notification> getAllNotification(Long userId);
}
