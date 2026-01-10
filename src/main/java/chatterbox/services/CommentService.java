package chatterbox.services;

import chatterbox.dtos.request.CreateCommentRequestDto;
import chatterbox.dtos.request.CreateMultipleCommentsDto;
import chatterbox.dtos.response.GetCommentByIdDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    void createComment(CreateCommentRequestDto dto);
    GetCommentByIdDto fetchCommentById(Long id);
    List<GetCommentByIdDto> fetchAllCommentOnAParticularPost(Long postId);
    void createMultipleComments(List<CreateMultipleCommentsDto> dtos);
}
