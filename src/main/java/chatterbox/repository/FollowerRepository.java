package chatterbox.repository;

import chatterbox.entities.Followers;
import chatterbox.entities.FollowersPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Followers, FollowersPrimaryKey> {
}
