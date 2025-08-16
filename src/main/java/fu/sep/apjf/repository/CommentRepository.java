package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAll();

    Optional<Comment> findById(Long id);

    List<Comment> findByPostId(Long postId);

    Long countByPostId(Long postId);

    @Query("""
        SELECT c.post.id, COUNT(c.id) 
        FROM Comment c 
        WHERE c.post.id IN :postIds 
        GROUP BY c.post.id
    """)
    List<Object[]> countByPostIdsRaw(List<Long> postIds);

    default Map<Long, Long> countByPostIds(List<Long> postIds) {
        return countByPostIdsRaw(postIds).stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));
    }
}
