package chatterbox.repository;

import chatterbox.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "Select * from comment where post_id = :postId", nativeQuery = true)
    List<Comment> findAllByPostId(@Param("postId") Long postId);

}
