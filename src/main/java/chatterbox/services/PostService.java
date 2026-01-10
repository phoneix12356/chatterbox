package chatterbox.services;

import chatterbox.dtos.request.CreateMultiplePostsDto;
import chatterbox.dtos.request.CreatePostRequestDto;
import chatterbox.dtos.response.GetPostByIdDto;

import java.util.List;

public interface PostService {
    void createPost(CreatePostRequestDto createPostRequestDto);

    GetPostByIdDto getPost(Long id);

    List<GetPostByIdDto> getAllThePostOfALLUser(int pageNumber, int pageSize);

    List<GetPostByIdDto> getAllThePostParticularUser(int pageNumber, int pageSize, Long postId);

    String saveAllPost(List<CreateMultiplePostsDto> dto);

    void deletePost(Long postId);

    void deleteEveryPostOfParticularUser(Long userId);
}
