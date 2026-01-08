package chatterbox.repository;

import chatterbox.entities.Follower;
import chatterbox.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
  List<Follower> findByFollowedUser(Users followedUser);

  List<Follower> findByFollower(Users follower);

  boolean existsByFollowerAndFollowedUser(Users follower, Users followedUser);

  @Query("SELECT f.followedUser FROM Follower f WHERE f.follower.id = :userId AND f.followedUser.id IN (SELECT f2.follower.id FROM Follower f2 WHERE f2.followedUser.id = :userId)")
  List<Users> findMutualFollowers(Long userId);
}
