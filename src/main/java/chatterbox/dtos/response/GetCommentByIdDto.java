package chatterbox.dtos.response;

import lombok.Data;

@Data
public class GetCommentByIdDto {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private String username;
    private java.time.LocalDateTime createdAt;
    private int likeCount;

    private Long parentCommentId;
}
