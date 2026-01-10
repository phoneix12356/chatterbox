package chatterbox.dtos.response;

import chatterbox.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private Long id;
  private String username;
  private String email;
  private String avatar;
  private int followerCount;
  private int followingCount;
  private String bio; // If bio exists
  private AccountStatus accountStatus;
  @JsonProperty("isOnline")
  private boolean isOnline;
  private LocalDateTime createdAt;
}
