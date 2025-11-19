package chatterbox.controller;

import chatterbox.dtos.request.CreateCommentRequestDto;
import chatterbox.dtos.request.CreateMultipleCommentsDto;
import chatterbox.dtos.response.GetCommentByIdDto;
import chatterbox.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")

public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> postComment(@RequestBody CreateCommentRequestDto dto) {
        commentService.createComment(dto);
        return ResponseEntity.ok("Successfully add comment to the post");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCommentByIdDto> getCommentBYId(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.fetchCommentById(id));
    }

    @GetMapping("/getallcommentonapost")
    public ResponseEntity<List<GetCommentByIdDto>> getAllCommentOnAParticularPost(@RequestParam long postId) {
        return ResponseEntity.ok(commentService.fetchAllCommentOnAParticularPost(postId));
    }

    @PostMapping("/batch")
    public ResponseEntity<String> multipleComments(@RequestBody List<CreateMultipleCommentsDto> dto) {
        commentService.createMultipleComments(dto);
        return ResponseEntity.ok("Successfully added to the database");
    }
}
