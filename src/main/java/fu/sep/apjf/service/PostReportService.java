package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.PostReportRequestDto;
import fu.sep.apjf.dto.response.PostReportResponseDto;
import fu.sep.apjf.entity.Post;
import fu.sep.apjf.entity.PostReport;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.PostReportMapper;
import fu.sep.apjf.repository.PostReportRepository;
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
public class PostReportService {

    private final PostReportRepository postReportRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public List<PostReportResponseDto> list() {
        return postReportRepo.findAll()
                .stream()
                .map(PostReportMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostReportResponseDto get(Long id) {
        return PostReportMapper.toDto(
                postReportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Post report không tồn tại"))
        );
    }

    public PostReportResponseDto create(@Valid PostReportRequestDto dto) {
        User user = userRepo.findById(Long.parseLong(dto.userId()))
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
        Post post = postRepo.findById(Long.parseLong(dto.postId()))
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));

        if (postReportRepo.existsByUserIdAndPostId(user.getId(), post.getId()))
            throw new IllegalArgumentException("Bạn đã báo cáo post này rồi");

        PostReport report = PostReportMapper.toEntity(dto, user, post);
        PostReport saved = postReportRepo.save(report);
        return PostReportMapper.toDto(saved);
    }

    public void delete(Long id) {
        PostReport report = postReportRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post report không tồn tại"));
        postReportRepo.delete(report);
    }
}

