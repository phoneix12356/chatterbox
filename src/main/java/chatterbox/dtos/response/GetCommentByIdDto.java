package chatterbox.dtos.response;

import lombok.Data;

@Data
public class GetCommentByIdDto {
    private String content;
    private Long postId;
    private Long userId;

    private Long parentCommentId;
}
