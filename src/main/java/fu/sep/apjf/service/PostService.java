package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.PostMapper;
import fu.sep.apjf.repository.PostRepository;
import fu.sep.apjf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    // ✅ Create new post
    public PostResponseDto createPost(PostRequestDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postMapper.toEntity(dto);
        post.setUser(user);
        postRepository.save(post);

        return postMapper.toDto(post, userId);
    }

    // ✅ Get post by ID
    public PostResponseDto getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postMapper.toDto(post, currentUserId);
    }

    // ✅ Get all posts
    public List<PostResponseDto> getAllPosts(Long currentUserId) {
        return postRepository.findAll().stream()
                .map(post -> postMapper.toDto(post, currentUserId))
                .collect(Collectors.toList());
    }

    // ✅ Update post content
    public PostResponseDto updatePost(Long postId, PostRequestDto dto, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (dto.content() != null) {
            post.setContent(dto.content());
        }

        postRepository.save(post);
        return postMapper.toDto(post, currentUserId);
    }

    // ✅ Delete post
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Optionally check ownership here
        postRepository.delete(post);
    }
}
