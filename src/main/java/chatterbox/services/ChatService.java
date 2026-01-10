package chatterbox.services;

import chatterbox.dtos.response.MessageDto;
import chatterbox.entities.Messages;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
  MessageDto processAndSaveMessage(Messages msg);

  List<MessageDto> getChatHistory(Long currentUserId, Long otherUserId, Pageable pageable);
}
