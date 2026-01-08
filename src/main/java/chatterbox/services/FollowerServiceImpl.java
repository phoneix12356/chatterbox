package chatterbox.services;

import chatterbox.entities.Notification;

import chatterbox.events.NotificationEvent;
import chatterbox.entities.FollowRequest;
import chatterbox.entities.Follower;
import chatterbox.entities.Users;
import chatterbox.enums.NotificationEnum;
import chatterbox.enums.RelatedEntityType;
import chatterbox.repository.FollowRequestRepository;
import chatterbox.repository.FollowerRepository;
import chatterbox.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FollowerServiceImpl implements FollowerService {
        private final FollowRequestRepository followRequestRepository;
        private final FollowerRepository followerRepository;
        private final UserRepository userRepository;

        private final ApplicationEventPublisher publisher;

        @Override
        @Transactional
        public void sendFollowRequest(Long targetUserId) {
                CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();
                Long requesterId = customUserDetail.getId();

                Users requester = userRepository.findById(requesterId)
                                .orElseThrow(() -> new RuntimeException("Requester not found"));
                Users target = userRepository.findById(targetUserId)
                                .orElseThrow(() -> new RuntimeException("Target user not found"));

                if (requester.getId().equals(target.getId())) {
                        throw new RuntimeException("You cannot follow yourself");
                }

                if (followerRepository.existsByFollowerAndFollowedUser(requester, target)) {
                        throw new RuntimeException("You are already following this user");
                }

                if (followRequestRepository.findByRequesterAndTarget(requester, target).isPresent()) {
                        throw new RuntimeException("Request already sent");
                }

                FollowRequest followRequest = new FollowRequest();
                followRequest.setRequester(requester);
                followRequest.setTarget(target);

                FollowRequest savedRequest = followRequestRepository.save(followRequest);

                Notification notification = new Notification();
                notification.setMessage(requester.getUsername() + " sent you a follow request");
                notification.setType(NotificationEnum.NEW_FOLLOWER);
                notification.setSenderId(requesterId);
                notification.setReceiverId(targetUserId);
                notification.setRelatedEntityId(savedRequest.getId());
                notification.setRelatedEntityType(RelatedEntityType.FollowRequest);
                notification.setReadStatus(false);

                NotificationEvent event = new NotificationEvent(this, notification);
                publisher.publishEvent(event);
        }

        @Override
        @Transactional
        public void acceptFollowRequest(Long requestId) {
                FollowRequest request = followRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Follow request not found"));

                Users requester = request.getRequester();
                Users target = request.getTarget();

                Follower follower = new Follower();
                follower.setFollower(requester);
                follower.setFollowedUser(target);

                followerRepository.save(follower);
                followRequestRepository.delete(request);

                requester.setFollowingCount(requester.getFollowingCount() + 1);
                target.setFollowerCount(target.getFollowerCount() + 1);

                userRepository.save(requester);
                userRepository.save(target);

                Notification notification = new Notification();
                notification.setMessage(target.getUsername() + " accepted your follow request");
                notification.setType(NotificationEnum.NEW_FOLLOWER);
                notification.setSenderId(target.getId());
                notification.setReceiverId(requester.getId());
                notification.setRelatedEntityId(follower.getId());
                notification.setRelatedEntityType(RelatedEntityType.Follower);
                notification.setReadStatus(false);

                NotificationEvent event = new NotificationEvent(this, notification);
                publisher.publishEvent(event);
        }

        @Override
        public List<Users> getFollowers(Long userId) {
                Users user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return followerRepository.findByFollowedUser(user).stream()
                                .map(Follower::getFollower)
                                .toList();
        }

        @Override
        public List<Users> getFollowing(Long userId) {
                Users user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return followerRepository.findByFollower(user).stream()
                                .map(Follower::getFollowedUser)
                                .toList();
        }

        @Override
        public List<Users> getMutualFollowers(Long userId) {
                return followerRepository.findMutualFollowers(userId);
        }
}
