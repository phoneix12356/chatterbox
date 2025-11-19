package chatterbox.controller;

import chatterbox.services.FollowerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/follower")
public class FollowerController {
    private FollowerService followerService;
    @PostMapping("/new-follower/{followerId}")
    public ResponseEntity<?> followRequest(@PathVariable Long followerId){
        followerService.newFollowerRequest(followerId);
        return ResponseEntity.ok("Sent follow request");
    }
}
