package chatterbox.controller;

import chatterbox.dtos.response.FollowRequestDto;
import chatterbox.dtos.response.UserDto;
import chatterbox.entities.FollowRequest;
import chatterbox.entities.Users;
import chatterbox.repository.FollowRequestRepository;
import chatterbox.repository.UserRepository;
import chatterbox.services.CustomUserDetail;
import chatterbox.services.FollowerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follower")
@AllArgsConstructor
@SuppressWarnings("null")
public class FollowerController {

    private final FollowerService followerService;
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;


    @PostMapping("/request/{targetUserId}")
    public ResponseEntity<?> sendFollowRequest(@PathVariable Long targetUserId) {

        followerService.sendFollowRequest(targetUserId);
        return ResponseEntity.ok("Follow request sent successfully");
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFollowRequest(@PathVariable Long requestId) {
        followerService.acceptFollowRequest(requestId);
        return ResponseEntity.ok("Follow request accepted");
    }

    @DeleteMapping("/request/{requestId}")
    public ResponseEntity<?> declineFollowRequest(@PathVariable Long requestId) {
        followRequestRepository.deleteById(requestId);
        return ResponseEntity.ok("Follow request declined");
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FollowRequestDto>> getPendingRequests(
            @AuthenticationPrincipal CustomUserDetail currentUser) {
        Users user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<FollowRequest> pendingRequests = followRequestRepository.findByTarget(user);

        List<FollowRequestDto> dtos = pendingRequests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private FollowRequestDto mapToDto(FollowRequest request) {
        return FollowRequestDto.builder()
                .id(request.getId())
                .createdAt(request.getCreatedAt())
                .requester(mapUserToDto(request.getRequester()))
                .build();
    }

    private UserDto mapUserToDto(Users user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .accountStatus(user.getAccountStatus())
                .isOnline(user.isOnline())
                .createdAt(user.getCreatedAt())
                .build();
    }


    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Long userId) {
        List<Users> followers = followerService.getFollowers(userId);
        return ResponseEntity.ok(followers.stream().map(this::mapUserToDto).collect(Collectors.toList()));
    }


    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable Long userId) {
        List<Users> following = followerService.getFollowing(userId);
        return ResponseEntity.ok(following.stream().map(this::mapUserToDto).collect(Collectors.toList()));
    }

    @GetMapping("/mutual")
    public ResponseEntity<List<UserDto>> getMutualFollowers(@AuthenticationPrincipal CustomUserDetail currentUser) {
        List<Users> mutuals = followerService.getMutualFollowers(currentUser.getId());
        return ResponseEntity.ok(mutuals.stream().map(this::mapUserToDto).collect(Collectors.toList()));
    }
}
