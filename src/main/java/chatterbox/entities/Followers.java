package chatterbox.entities;

import chatterbox.enums.FollowRequestStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data

public class Followers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user who i am sending the request whom i want to follow
    @ManyToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;
    // it is me who is sending the request
    @ManyToOne
    @MapsId
    @JoinColumn(name = "follower_id")
    private Users follower;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime acceptedAt;
    @Enumerated(EnumType.STRING)
    private FollowRequestStatus followRequestStatus = FollowRequestStatus.PENDING;

    @PreUpdate
    public void updateAt(){
        updatedAt = LocalDateTime.now();
        if(followRequestStatus == FollowRequestStatus.ACCEPTED && acceptedAt == null){
            acceptedAt = LocalDateTime.now();
        }
    }
}
