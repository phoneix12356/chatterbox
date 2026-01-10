package chatterbox.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FollowRequestDto {
  private Long id;
  private UserDto requester;
  private LocalDateTime createdAt;
}
