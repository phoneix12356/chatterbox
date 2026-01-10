package chatterbox.controller;

import chatterbox.dtos.response.MessageDto;
import chatterbox.entities.Messages;
import chatterbox.entities.Users;
import chatterbox.repository.UserRepository;
import chatterbox.services.ChatService;
import chatterbox.services.CustomUserDetail;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class ChatController {

        private final SimpMessagingTemplate simpMessagingTemplate;
        private final SimpUserRegistry userRegistry;
        private final ChatService chatService;
        private final UserRepository userRepository;


        @MessageMapping("/send")
        public void sendMessage(@Payload Messages msg) {
                // Save and process message via service
                MessageDto responseDto = chatService.processAndSaveMessage(msg);

                // Get receiver endpoint (Email)
                Users receiver = userRepository.findById(msg.getReceiverId())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with id: " + msg.getReceiverId()));

                // Send to receiver if online
                if (checkIfUserIsOnline(receiver.getEmail())) {
                        simpMessagingTemplate.convertAndSendToUser(
                                        receiver.getEmail(),
                                        "/queue/chat",
                                        responseDto);
                }
        }


        @GetMapping("/api/chat/history/{userId}")
        @ResponseBody
        public ResponseEntity<List<MessageDto>> getChatHistory(
                        @PathVariable("userId") Long otherUserId,
                        @RequestParam(defaultValue = "0") int pageNumber,
                        @RequestParam(defaultValue = "20") int pageSize,
                        @AuthenticationPrincipal CustomUserDetail currentUser) {

                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                List<MessageDto> dtos = chatService.getChatHistory(currentUser.getId(), otherUserId, pageable);

                return ResponseEntity.ok(dtos);
        }

        private boolean checkIfUserIsOnline(String email) {
                return userRegistry.getUser(email) != null;
        }
}
