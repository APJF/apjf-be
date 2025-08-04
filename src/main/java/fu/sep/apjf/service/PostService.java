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

    // ✅ Get all posts
    public List<PostResponseDto> list(Long currentUserId) {
        return postRepository.findAll().stream()
                .map(post -> postMapper.toDto(post, currentUserId))
                .collect(Collectors.toList());
    }

    // ✅ Get post by ID
    public PostResponseDto get(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postMapper.toDto(post, currentUserId);
    }

    // ✅ Create new post
    public PostResponseDto create(PostRequestDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postMapper.toEntity(dto);
        post.setUser(user);
        postRepository.save(post);

        return postMapper.toDto(post, userId);
    }

    // ✅ Update post content
    public PostResponseDto update(Long postId, PostRequestDto dto, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (dto.content() != null) {
            post.setContent(dto.content());
        }

        postRepository.save(post);
        return postMapper.toDto(post, currentUserId);
    }

    // ✅ Delete post
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(post);
    }
}
