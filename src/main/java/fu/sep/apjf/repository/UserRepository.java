package fu.sep.apjf.repository;

import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAll();
    @Query("""
        SELECT u
        FROM User u
        WHERE u.enabled = true
          AND u.createAt >= :startDate
    """)
    List<User> findEnabledUsersFrom(@Param("startDate") LocalDateTime startDate);



    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countTotalEnabledUsers();
}
