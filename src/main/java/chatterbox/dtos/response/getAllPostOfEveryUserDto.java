package chatterbox.dtos.response;

import lombok.Data;

import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class getAllPostOfEveryUserDto extends GetPostByIdDto {
    private int pageNumber;
    private int pageSize;
}
