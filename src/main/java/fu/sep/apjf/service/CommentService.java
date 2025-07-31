package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CommentRequestDto;
import fu.sep.apjf.dto.response.CommentResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.CommentMapper;
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
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public List<CommentResponseDto> list() {
        return commentRepo.findAll()
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponseDto get(Long id) {
        return CommentMapper.toDto(
                commentRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment không tồn tại"))
        );
    }

    public CommentResponseDto create(@Valid CommentRequestDto dto) {
        User user = userRepo.findById(Long.parseLong(dto.userId()))
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
        Post post = postRepo.findById(Long.parseLong(dto.postId()))
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));

        Comment comment = CommentMapper.toEntity(dto, user, post);
        Comment saved = commentRepo.save(comment);
        return CommentMapper.toDto(saved);
    }

    public CommentResponseDto update(Long id, @Valid CommentRequestDto dto) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment không tồn tại"));

        comment.setContent(dto.content());
        comment.setUser(userRepo.findById(Long.parseLong(dto.userId()))
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại")));
        comment.setPost(postRepo.findById(Long.parseLong(dto.postId()))
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại")));

        Comment updated = commentRepo.save(comment);
        return CommentMapper.toDto(updated);
    }

    public void delete(Long id) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment không tồn tại"));
        commentRepo.delete(comment);
    }
}

