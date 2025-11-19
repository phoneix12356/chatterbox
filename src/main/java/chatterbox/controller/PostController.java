package chatterbox.controller;

import chatterbox.dtos.request.CreateMultiplePostsDto;
import chatterbox.dtos.request.CreatePostRequestDto;
import chatterbox.dtos.response.GetPostByIdDto;
import chatterbox.entities.Post;
import chatterbox.services.LikeService;
import chatterbox.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequestDto createPostRequest) {
        try {
            postService.createPost(createPostRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Fixed: Changed "/" to "/{postId}" for RESTful design
    @GetMapping("/{postId}")
    public ResponseEntity<GetPostByIdDto> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping
    public ResponseEntity<List<GetPostByIdDto>> getAllPosts(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize
    ) {
        return ResponseEntity.ok(postService.getAllThePostOfALLUser(pageNumber, pageSize));
    }

    // Fixed: Renamed endpoint and changed parameter name
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GetPostByIdDto>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "3") int pageSize
    ) {
        return ResponseEntity.ok(postService.getAllThePostParticularUser(pageNumber, pageSize, userId));
    }

    // Fixed: Changed to POST and better naming
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        likeService.like(postId);
        return ResponseEntity.ok("Like added successfully");
    }

    // Fixed: Changed to DELETE for unlike action
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId) {
        likeService.dislike(postId);
        return ResponseEntity.ok("Like removed successfully");
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<String> sharePost(@PathVariable Long postId) {
        likeService.sharePost(postId);
        return ResponseEntity.ok("Share count increased successfully");
    }

    // Fixed: Better naming
    @PostMapping("/batch")
    public ResponseEntity<String> createMultiplePosts(@RequestBody List<CreateMultiplePostsDto> posts) {
        return ResponseEntity.ok(postService.saveAllPost(posts));
    }

    // Fixed: Simplified endpoint name and used path variable
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    // Fixed: Better endpoint naming and spelling
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteAllPostsByUser(@PathVariable Long userId) {
        postService.deleteEveryPostOfParticularUser(userId);
        return ResponseEntity.ok("Successfully deleted all posts of user");
    }
}
