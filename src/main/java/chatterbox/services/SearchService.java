package chatterbox.services;

import chatterbox.dtos.response.UserDto;
import java.util.List;

public interface SearchService {
  List<UserDto> searchUsers(String query);
}
