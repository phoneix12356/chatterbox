package chatterbox.controller;

import chatterbox.dtos.response.UserDto;
import chatterbox.services.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @GetMapping("/users")
  public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
    return ResponseEntity.ok(searchService.searchUsers(query));
  }
}
