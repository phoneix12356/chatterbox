package chatterbox.dtos.request;

import chatterbox.enums.NotificationEnum;
import chatterbox.enums.RelatedEntityType;
import lombok.Builder;
import lombok.Data;
/*
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
 */


@Builder
@Data
public class CreateNotificationDto {
    private String message;
    private NotificationEnum type;
    private Long senderId;
    private Long receiverId;
    private boolean readStatus;
    private Long relatedEntityId;
    private RelatedEntityType relatedEntityType;

}
