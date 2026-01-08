package chatterbox.repository;

import chatterbox.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE post SET comment_count = comment_count + 1 WHERE id = :id", nativeQuery = true)
    void updateCommentCount(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE post SET like_count = like_count + 1 WHERE id = :id", nativeQuery = true)
    void increaseLike(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE post SET like_count = like_count - 1 WHERE id = :id", nativeQuery = true)
    void decreaseLike(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE post SET share_count = share_count + 1 WHERE id = :id", nativeQuery = true)
    void increaseShareCount(@Param("id") Long id);

    @Query(value = "SELECT * FROM post WHERE user_id = :userId LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<Post> findPostsByUserIdUsingPagination(
            @Param("userId") Long userId,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM post WHERE user_id = :userId", nativeQuery = true)
    void deleteEveryPostOfUserByUserId(@Param("userId") Long userId);


    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + :increment WHERE p.id = :postId")
    void updateCommentCountByIncrement(@Param("postId") Long postId, @Param("increment") int increment);
}
