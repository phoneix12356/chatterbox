package chatterbox.controller;

import chatterbox.dtos.request.UpdateUserProfileDto;
import chatterbox.dtos.response.UserDto;
import chatterbox.services.CustomUserDetail;
import chatterbox.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetail currentUser) {
    return ResponseEntity.ok(userService.getCurrentUser(currentUser.getId()));
  }


  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserById(userId));
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<String> updateUserProfile(@RequestBody UpdateUserProfileDto dto, @PathVariable Long userId) {
    userService.updateUserProfile(userId, dto);
    return ResponseEntity.ok("Successfully updated");
  }

}
