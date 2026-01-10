package chatterbox.repository;

import chatterbox.entities.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Long> {

  @Query("""
      SELECT m
      FROM Messages m
      WHERE (
         (m.senderId = :user1Id AND m.receiverId = :user2Id)
         OR
         (m.senderId = :user2Id AND m.receiverId = :user1Id)
      )
      ORDER BY m.createdAt DESC
      """)
  Page<Messages> findChatHistory(
      @Param("user1Id") Long user1Id,
      @Param("user2Id") Long user2Id,
      Pageable pageable);
}
