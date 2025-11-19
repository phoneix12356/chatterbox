package chatterbox.services;

import chatterbox.dtos.request.CreateCommentRequestDto;
import chatterbox.dtos.request.CreateMultipleCommentsDto;
import chatterbox.dtos.response.GetCommentByIdDto;
import chatterbox.entities.Comment;
import chatterbox.entities.Post;
import chatterbox.entities.Users;
import chatterbox.exceptions.EntityNotFoundException;
import chatterbox.repository.CommentRepository;
import chatterbox.repository.PostRepository;
import chatterbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void createComment(CreateCommentRequestDto dto) {
        // Get authenticated user
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Users usr = userRepository.findById(customUserDetail.getId()).orElseThrow(() -> new RuntimeException("User not found with id: " + customUserDetail.getId()));

        var post = postRepository.findById(dto.getPostId()).orElseThrow(() -> new RuntimeException("Post not found with id: " + dto.getPostId()));

        Comment comment = modelMapper.map(dto, Comment.class);

        comment.setUser(usr);
        comment.setPost(post);

        if (dto.getParentCommentId() != null) {
            commentRepository.findById(dto.getParentCommentId()).ifPresent(comment::setParentComment);
        }

        commentRepository.save(comment);
        postRepository.updateCommentCount(dto.getPostId());
    }

    public GetCommentByIdDto fetchCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found with id " + id));
        return this.modelMapper.map(comment, GetCommentByIdDto.class);
    }

    public List<GetCommentByIdDto> fetchAllCommentOnAParticularPost(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream().map(comment -> this.modelMapper.map(comment, GetCommentByIdDto.class)).collect(Collectors.toList());
    }


    @Transactional
    public void createMultipleComments(List<CreateMultipleCommentsDto> dtos) {
        List<Comment> comments = dtos.stream().map(data -> {
            Comment comment = modelMapper.map(data, Comment.class);
            Users user = userRepository.findById(data.getUserRef())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + data.getUserRef()));
            Post post = postRepository.findById(data.getPostRef())
                    .orElseThrow(() -> new EntityNotFoundException("Post not found: " + data.getPostRef()));
            comment.setUser(user);
            comment.setPost(post);

            return comment;
        }).collect(Collectors.toList());

             List<Comment> saved = commentRepository.saveAll(comments);


        boolean needUpdate = false;
        for (int i = 0; i < dtos.size(); i++) {
            Long parentRef = dtos.get(i).getParentCommentRef();
            if (parentRef != null) {
                Comment parent = commentRepository.findById(parentRef)
                        .orElseThrow(() -> new EntityNotFoundException("Parent comment not found: " + parentRef));
                Comment child = saved.get(i);
                child.setParentComment(parent);
                needUpdate = true;
            }
        }

        if (needUpdate) {
            commentRepository.saveAll(saved);
        }
    }


}
