package chatterbox.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "requester_id")
  private Users requester;

  @ManyToOne
  @JoinColumn(name = "target_id")
  private Users target;

  @Column(updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
