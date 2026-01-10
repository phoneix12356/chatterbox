package chatterbox.events;

import chatterbox.entities.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private Notification notification;
    public NotificationEvent(Object source , Notification notification){
        super(notification);
        this.notification = notification;
    }
}
