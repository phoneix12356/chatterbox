package chatterbox.entities;
import chatterbox.enums.NotificationEnum;
import chatterbox.enums.RelatedEntityType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Entity
public class Notification{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private NotificationEnum type;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private Boolean readStatus = false;

    private Long senderId;
    private Long receiverId;
    private Long relatedEntityId;
    @Enumerated(EnumType.STRING)
    private RelatedEntityType relatedEntityType;
}

