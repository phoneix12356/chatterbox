package chatterbox.services;

import chatterbox.dtos.request.UpdateUserProfileDto;
import chatterbox.dtos.response.UserDto;
import chatterbox.entities.Users;
import chatterbox.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(user -> modelMapper.map(user, UserDto.class))
        .collect(Collectors.toList());
  }

  @Override
  public UserDto getCurrentUser(Long currentUserId) {
    Users user = userRepository.findById(currentUserId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return mapToDto(user);
  }

  @Override
  public UserDto getUserById(Long userId) {
    Users user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    return mapToDto(user);
  }

  @Override
  @Transactional
  public void updateUserProfile(Long userId, UpdateUserProfileDto dto) {
    userRepository.updateUserProfileById(userId, dto.getUsername(), dto.getEmail(), dto.getAvatar());
  }

  private UserDto mapToDto(Users user) {
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
}
