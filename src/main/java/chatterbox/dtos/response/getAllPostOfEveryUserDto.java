package chatterbox.dtos.response;

import lombok.Data;

@Data
public class getAllPostOfEveryUserDto extends GetPostByIdDto {
    private int pageNumber;
    private int pageSize;
}
