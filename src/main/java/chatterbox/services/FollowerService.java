package chatterbox.services;

import chatterbox.entities.Users;

import java.util.List;

public interface FollowerService {
    void sendFollowRequest(Long targetUserId);

    void acceptFollowRequest(Long requestId);

    List<Users> getFollowers(Long userId);

    List<Users> getFollowing(Long userId);

    List<Users> getMutualFollowers(Long userId);
}
