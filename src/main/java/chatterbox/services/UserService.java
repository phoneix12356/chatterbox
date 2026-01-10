package chatterbox.services;

import chatterbox.dtos.request.UpdateUserProfileDto;
import chatterbox.dtos.response.UserDto;
import java.util.List;

public interface UserService {
  List<UserDto> getAllUsers();

  UserDto getCurrentUser(Long currentUserId);

  UserDto getUserById(Long userId);

  void updateUserProfile(Long userId, UpdateUserProfileDto dto);
}
