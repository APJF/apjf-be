package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.PostLikeResponseDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.PostMapper;
import fu.sep.apjf.repository.CommentRepository;
import fu.sep.apjf.repository.PostLikeRepository;
import fu.sep.apjf.repository.PostRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private static final String POST_NOT_FOUND = "Post không tồn tại";

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostMapper postMapper;
    private final CommentRepository commentRepo; // Thêm CommentRepository
    private final PostLikeService postLikeService;
    private final PostLikeRepository postLikeRepo;

    @Transactional(readOnly = true)
    public List<PostResponseDto> list(Long currentUserId) {
        List<Post> posts = postRepo.findAllWithUser();
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Map<Long, Long> commentsCountMap = commentRepo.countByPostIds(postIds);
        Map<Long, Long> likesCountMap = postLikeRepo.countByPostIds(postIds);
        Map<Long, Boolean> likedMap = postLikeRepo.hasUserLikedByPostIds(postIds, currentUserId);

        return posts.stream()
                .map(post -> {
                    Long commentsCount = commentsCountMap.getOrDefault(post.getId(), 0L);
                    int totalLikes = likesCountMap.getOrDefault(post.getId(), 0L).intValue();
                    boolean likedByUser = likedMap.getOrDefault(post.getId(), false);
                    PostLikeResponseDto likeInfo = new PostLikeResponseDto(likedByUser, totalLikes);
                    return postMapper.toDetailDto(post, commentsCount, likeInfo);
                })
                .toList();
    }



    @Transactional(readOnly = true)
    public PostResponseDto get(Long id, Long currentUserId) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
        Long commentsCount = commentRepo.countByPostId(post.getId());
        int totalLikes = postLikeService.countLikes(post.getId());
        boolean likedByUser = postLikeService.hasUserLiked(post.getId(), currentUserId);
        PostLikeResponseDto likeInfo = new PostLikeResponseDto(likedByUser, totalLikes );
        return postMapper.toDetailDto(post, commentsCount, likeInfo);
    }


    public PostResponseDto create(@Valid PostRequestDto dto, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
        Post post = postMapper.toEntity(dto);
        post.setUser(user);

        Post saved = postRepo.save(post);
        Long commentsCount = 0L; // Post mới tạo chưa có comment nào
        return postMapper.toDto(saved, commentsCount);
    }

    public PostResponseDto update(Long id, @Valid PostRequestDto dto, Long userId) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));

        post.setContent(dto.content());
        post.setUser(userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại")));

        Post saved = postRepo.save(post);
        Long commentsCount = commentRepo.countByPostId(saved.getId());
        return postMapper.toDto(saved, commentsCount);
    }

    public void delete(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
        postRepo.delete(post);
    }
}
