package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CommentReportRequestDto;
import fu.sep.apjf.dto.response.CommentReportResponseDto;
import fu.sep.apjf.entity.Comment;
import fu.sep.apjf.entity.CommentReport;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.CommentReportMapper;
import fu.sep.apjf.repository.CommentReportRepository;
import fu.sep.apjf.repository.CommentRepository;
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
public class CommentReportService {

    private final CommentReportRepository commentReportRepo;
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public List<CommentReportResponseDto> list() {
        return commentReportRepo.findAll()
                .stream()
                .map(CommentReportMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentReportResponseDto get(Long id) {
        return CommentReportMapper.toDto(
                commentReportRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment report không tồn tại"))
        );
    }

    public CommentReportResponseDto create(@Valid CommentReportRequestDto dto) {
        User user = userRepo.findById(Long.parseLong(dto.userId()))
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
        Comment comment = commentRepo.findById(Long.parseLong(dto.commentId()))
                .orElseThrow(() -> new EntityNotFoundException("Comment không tồn tại"));

        if (commentReportRepo.existsByUserIdAndCommentId(user.getId(), comment.getId()))
            throw new IllegalArgumentException("Bạn đã báo cáo comment này rồi");

        CommentReport report = CommentReportMapper.toEntity(dto, user, comment);
        CommentReport saved = commentReportRepo.save(report);
        return CommentReportMapper.toDto(saved);
    }

    public void delete(Long id) {
        CommentReport report = commentReportRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment report không tồn tại"));
        commentReportRepo.delete(report);
    }
}

