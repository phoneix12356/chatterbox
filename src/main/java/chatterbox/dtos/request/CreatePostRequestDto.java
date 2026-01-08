package chatterbox.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequestDto {

    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 1000, message = "Post content cannot exceed 1000 characters")
    private String content;

    @Size(max = 5, message = "You can attach at most 5 URLs")
    private List<
            String
            > urls;
}
