package fu.sep.apjf.service;

import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostLike;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.repository.PostLikeRepository;
import fu.sep.apjf.repository.PostRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    /**
     * Người dùng like một post nếu chưa like.
     */
    public void likePost(Long postId, Long userId) {
        if (postLikeRepo.existsByPostIdAndUserId(postId, userId)) {
            log.info("User {} đã like post {}", userId, postId);
            return;
        }

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        like.setLikedAt(Instant.now());

        postLikeRepo.save(like);
        log.info("User {} đã like post {}", userId, postId);
    }

    /**
     * Người dùng bỏ like post.
     */
    public void unlikePost(Long postId, Long userId) {
        if (postLikeRepo.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepo.deleteByPostIdAndUserId(postId, userId);
            log.info("User {} đã bỏ like post {}", userId, postId);
        } else {
            log.info("User {} chưa từng like post {}, không cần unlike", userId, postId);
        }
    }

    /**
     * Kiểm tra user hiện tại đã like post chưa.
     */
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, Long userId) {
        return postLikeRepo.existsByPostIdAndUserId(postId, userId);
    }

    /**
     * Đếm tổng số lượt like của một post.
     */
    @Transactional(readOnly = true)
    public int countLikes(Long postId) {
        return postLikeRepo.countByPostId(postId);
    }
}

