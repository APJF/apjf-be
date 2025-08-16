package fu.sep.apjf.repository;

import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostLike;
import fu.sep.apjf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Tìm like theo User + Post
    Optional<PostLike> findByUserAndPost(User user, Post post);

    // Đếm tổng số like của một Post
    int countByPost(Post post);

    // Xóa like theo User + Post (unlike)
    void deleteByUserAndPost(User user, Post post);

    // Kiểm tra User đã like Post chưa
    boolean existsByUserAndPost(User user, Post post);

    @Query("""
        SELECT l.post.id, COUNT(l.id) 
        FROM PostLike l 
        WHERE l.post.id IN :postIds
        GROUP BY l.post.id
    """)
    List<Object[]> countByPostIdsRaw(List<Long> postIds);

    default Map<Long, Long> countByPostIds(List<Long> postIds) {
        return countByPostIdsRaw(postIds).stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));
    }

    @Query("""
        SELECT l.post.id, COUNT(l.id) > 0 
        FROM PostLike l 
        WHERE l.post.id IN :postIds AND l.user.id = :userId
        GROUP BY l.post.id
    """)
    List<Object[]> hasUserLikedByPostIdsRaw(List<Long> postIds, Long userId);

    default Map<Long, Boolean> hasUserLikedByPostIds(List<Long> postIds, Long userId) {
        return hasUserLikedByPostIdsRaw(postIds, userId).stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Boolean) r[1]
                ));
    }
}
