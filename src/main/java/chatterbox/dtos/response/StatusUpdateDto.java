package chatterbox.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDto {
  private String email;
  private String status; // "ONLINE" or "OFFLINE"
}
