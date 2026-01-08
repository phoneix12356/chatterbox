package chatterbox.dtos.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetPostByIdDto implements Serializable {
    private Long id;
    private String content;
    private List<String> urls;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private Long userId;
    private String userName;
    private String userAvatar;

}
