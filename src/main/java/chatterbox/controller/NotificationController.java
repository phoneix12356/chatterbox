package chatterbox.controller;

import chatterbox.dtos.request.CreateNotificationDto;
import chatterbox.entities.Followers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    @MessageMapping("/notification/followers")
    @SendToUser("/notification")
    public void followRequestNotification(CreateNotificationDto createNotificationDto){
       Long reciverId = createNotificationDto.getReceiverId();
       Long senderId = createNotificationDto.getReceiverId();
    }
}
