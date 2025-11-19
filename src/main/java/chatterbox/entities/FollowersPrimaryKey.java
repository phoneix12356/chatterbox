package chatterbox.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class FollowersPrimaryKey implements Serializable {
    private Long userId;
    private Long followerId;
}
