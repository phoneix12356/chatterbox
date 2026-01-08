package chatterbox.repository;

import chatterbox.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    List<Users> findByUsernameContainingIgnoreCase(String query);


    @Modifying
    @Query("""
                UPDATE Users u
                SET u.username = coalesce(:username, u.username),
                    u.email    = coalesce(:email,    u.email),
                    u.avatar   = coalesce(:avatar,   u.avatar)
                WHERE u.id = :userId
            """)
    void updateUserProfileById(
            @Param("userId") Long userId,
            @Param("username") String username,
            @Param("email") String email,
            @Param("avatar") String avatar);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.isOnline = false")
    void updateAllUsersToOffline();
}
