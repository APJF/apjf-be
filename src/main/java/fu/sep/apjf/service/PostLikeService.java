package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.NotificationRequestDto;
import fu.sep.apjf.dto.request.PostLikeRequestDto;
import fu.sep.apjf.dto.response.PostLikeResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostLike;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.PostLikeMapper;
import fu.sep.apjf.repository.PostLikeRepository;
import fu.sep.apjf.repository.PostRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostLikeService {

    private static final String POST_NOT_FOUND = "Post không tồn tại";
    private static final String USER_NOT_FOUND = "User không tồn tại";

    private final PostLikeRepository postLikeRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostLikeMapper postLikeMapper;
    private final NotificationService notificationService;

    public PostLikeResponseDto toggleLike(PostLikeRequestDto dto, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        Post post = postRepo.findById(dto.postId())
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));

        boolean exists = postLikeRepo.existsByUserAndPost(user, post);

        PostLike postLike;
        if (exists) {
            postLikeRepo.deleteByUserAndPost(user, post);
        } else {
            postLike = new PostLike();
            postLike.setUser(user);
            postLike.setPost(post);
            postLikeRepo.save(postLike);

            NotificationRequestDto noti= new NotificationRequestDto(user.getUsername() + " đã thích bài viết của bạn.", userId, post.getId());
            notificationService.create(noti);
        }

        int totalLikes = postLikeRepo.countByPost(post);
        return postLikeMapper.toDto(!exists, totalLikes);
    }

    @Transactional(readOnly = true)
    public int countLikes(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
        return postLikeRepo.countByPost(post);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long postId, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
        return postLikeRepo.existsByUserAndPost(user, post);
    }
}

