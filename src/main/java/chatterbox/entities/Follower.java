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
public class Follower {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // The user who is following
  @ManyToOne
  @JoinColumn(name = "follower_id")
  private Users follower;

  // The user being followed
  @ManyToOne
  @JoinColumn(name = "followed_user_id")
  private Users followedUser;

  @Column(updatable = false)
  private LocalDateTime becameFollowerAt = LocalDateTime.now();
}
