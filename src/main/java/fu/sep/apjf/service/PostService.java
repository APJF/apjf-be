package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.PostMapper;
import fu.sep.apjf.repository.CommentRepository;
import fu.sep.apjf.repository.PostRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostMapper postMapper;
    private final CommentRepository commentRepo; // Thêm CommentRepository

    @Transactional(readOnly = true)
    public List<PostResponseDto> list() {
        return postRepo.findAll()
                .stream()
                .map(post -> {
                    Long commentsCount = commentRepo.countByPostId(post.getId());
                    return postMapper.toDto(post, commentsCount);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponseDto get(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));
        Long commentsCount = commentRepo.countByPostId(post.getId());
        return postMapper.toDto(post, commentsCount);
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
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));

        post.setContent(dto.content());
        post.setUser(userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại")));

        Post saved = postRepo.save(post);
        Long commentsCount = commentRepo.countByPostId(saved.getId());
        return postMapper.toDto(saved, commentsCount);
    }

    public void delete(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));
        postRepo.delete(post);
    }
}
