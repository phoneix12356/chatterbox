package chatterbox.dtos.request;


import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequestDto {
    private String content;
    private List<String> urls;
}