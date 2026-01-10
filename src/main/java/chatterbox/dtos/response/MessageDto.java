package chatterbox.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class MessageDto {
  private Long id;
  private String senderId;
  private String receiverId;
  private Long senderIntId;
  private Long receiverIntId;
  private String message;
  private String url;
  private Instant createdAt;
}
