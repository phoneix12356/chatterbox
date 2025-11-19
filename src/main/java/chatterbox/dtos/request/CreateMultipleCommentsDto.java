package chatterbox.dtos.request;

import lombok.Data;

@Data
public class CreateMultipleCommentsDto {
    private Long userRef;
    private Long postRef;
    private String content;
    private Long parentCommentRef;
}