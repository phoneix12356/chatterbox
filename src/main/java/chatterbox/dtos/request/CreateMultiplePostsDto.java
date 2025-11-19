package chatterbox.dtos.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateMultiplePostsDto {
    private Long userId;
    private String content;
    private List<String> url;
}
