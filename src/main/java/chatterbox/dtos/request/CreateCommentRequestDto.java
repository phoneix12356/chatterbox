package chatterbox.dtos.request;

import lombok.Data;

@Data
public class CreateCommentRequestDto {
  private String content;
  private Long postRef;
  private Long parentCommentRef;
}