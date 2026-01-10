package chatterbox.repository;

import chatterbox.entities.FollowRequest;
import chatterbox.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
  Optional<FollowRequest> findByRequesterAndTarget(Users requester, Users target);

  List<FollowRequest> findByTarget(Users target);
}
