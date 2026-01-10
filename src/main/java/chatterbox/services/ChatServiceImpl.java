package chatterbox.services;

import chatterbox.dtos.response.MessageDto;
import chatterbox.entities.Messages;

import chatterbox.repository.MessageRepository;
import chatterbox.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public MessageDto processAndSaveMessage(Messages msg) {
    userRepository.findById(msg.getReceiverId())
        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + msg.getReceiverId()));

    userRepository.findById(msg.getSenderId())
        .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));

    Messages savedMsg = messageRepository.save(msg);

    return mapToDto(savedMsg);
  }

  @Override
  public List<MessageDto> getChatHistory(Long currentUserId, Long otherUserId, Pageable pageable) {
    Page<Messages> pageResult = messageRepository.findChatHistory(currentUserId, otherUserId, pageable);

    return pageResult.getContent().stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  private MessageDto mapToDto(Messages msg) {
    return MessageDto.builder()
        .id(msg.getId())
        .senderId(String.valueOf(msg.getSenderId()))
        .receiverId(String.valueOf(msg.getReceiverId()))
        .senderIntId(msg.getSenderId())
        .receiverIntId(msg.getReceiverId())
        .message(msg.getMesssage())
        .url(msg.getUrl())
        .createdAt(msg.getCreatedAt())
        .build();
  }
}
