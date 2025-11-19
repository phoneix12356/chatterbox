package chatterbox.services;

import chatterbox.dtos.request.CreateNotificationDto;
import chatterbox.entities.Followers;
import chatterbox.entities.Users;
import chatterbox.enums.NotificationEnum;
import chatterbox.enums.RelatedEntityType;
import chatterbox.repository.FollowerRepository;
import chatterbox.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class FollowerService {
    private final FollowerRepository repo;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void newFollowerRequest(Long followerId) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = customUserDetail.getId();
        Followers followers = new Followers();
        Users requestSender = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Users requestAcceptor = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("User not found"));
        followers.setFollower(requestSender);
        followers.setUser(requestAcceptor);
        Followers savedfollower = repo.save(followers);
        CreateNotificationDto.builder().message("new follow request have been comming").relatedEntityId(savedfollower.getId()).type(NotificationEnum.NEW_FOLLOWER).relatedEntityType(RelatedEntityType.Followers).senderId(userId).receiverId(followerId).build();

    }
}
