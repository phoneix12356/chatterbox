package chatterbox.services;

import chatterbox.dtos.request.CreateCommentRequestDto;
import chatterbox.dtos.request.CreateMultipleCommentsDto;
import chatterbox.dtos.response.GetCommentByIdDto;
import chatterbox.entities.Comment;
import chatterbox.entities.Notification;
import chatterbox.entities.Post;
import chatterbox.entities.Users;
import chatterbox.exceptions.EntityNotFoundException;
import chatterbox.repository.CommentRepository;
import chatterbox.repository.PostRepository;
import chatterbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import chatterbox.events.NotificationEvent;
import chatterbox.enums.NotificationEnum;
import chatterbox.enums.RelatedEntityType;
import org.springframework.context.ApplicationEventPublisher;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void createComment(CreateCommentRequestDto dto) {

        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Users usr = userRepository.findById(customUserDetail.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + customUserDetail.getId()));

        var post = postRepository.findById(dto.getPostRef())
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + dto.getPostRef()));

        Comment comment = modelMapper.map(dto, Comment.class);

        comment.setUser(usr);
        comment.setPost(post);

        if (dto.getParentCommentRef() != null) {
            commentRepository.findById(dto.getParentCommentRef()).ifPresent(comment::setParentComment);
        }

        commentRepository.save(comment);

        if (!usr.getId().equals(post.getUser().getId())) {
            Notification notification = new Notification();
            notification.setMessage(usr.getUsername() + " commented on your post");
            notification.setType(NotificationEnum.COMMENT);
            notification.setSenderId(usr.getId());
            notification.setReceiverId(post.getUser().getId());
            notification.setRelatedEntityId(post.getId());
            notification.setRelatedEntityType(RelatedEntityType.Post);
            notification.setReadStatus(false);

            NotificationEvent event = new NotificationEvent(this, notification);
            publisher.publishEvent(event);
        }

        postRepository.updateCommentCount(post.getId());
    }

    public GetCommentByIdDto fetchCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + id));
        return mapToDto(comment);
    }

    public List<GetCommentByIdDto> fetchAllCommentOnAParticularPost(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private GetCommentByIdDto mapToDto(Comment comment) {
        GetCommentByIdDto dto = this.modelMapper.map(comment, GetCommentByIdDto.class);
        if (comment.getUser() != null) {
            dto.setUsername(comment.getUser().getUsername());
            dto.setUserId(comment.getUser().getId());
        }
        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }
        return dto;
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
