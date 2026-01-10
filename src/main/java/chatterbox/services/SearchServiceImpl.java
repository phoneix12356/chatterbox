package chatterbox.services;

import chatterbox.dtos.response.UserDto;
import chatterbox.entities.Users;
import chatterbox.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

  private final UserRepository userRepository;

  @Override
  public List<UserDto> searchUsers(String query) {
    if (query == null || query.trim().isEmpty()) {
      return List.of();
    }
    List<Users> users = userRepository.findByUsernameContainingIgnoreCase(query.trim());
    return users.stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
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
