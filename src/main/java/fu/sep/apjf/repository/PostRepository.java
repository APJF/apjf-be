package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAll();

    Optional<Post> findById(Long id);

    @Query("""
        SELECT p 
        FROM Post p
        JOIN FETCH p.user u
        ORDER BY p.createdAt DESC
    """)
    List<Post> findAllWithUser();

}
