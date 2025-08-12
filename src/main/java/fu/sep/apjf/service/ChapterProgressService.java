package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ChapterProgressRequestDto;
import fu.sep.apjf.dto.response.ChapterProgressResponseDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.ChapterProgress;
import fu.sep.apjf.entity.ChapterProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.ChapterProgressMapper;
import fu.sep.apjf.repository.ChapterProgressRepository;
import fu.sep.apjf.repository.ChapterRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChapterProgressService {

    private final ChapterProgressRepository chapterProgressRepo;
    private final ChapterRepository chapterRepo;
    private final UserRepository userRepo;
    private final ChapterProgressMapper chapterProgressMapper;

    @Transactional(readOnly = true)
    public List<ChapterProgressResponseDto> findByUserId(Long userId) {
        return chapterProgressRepo.findByUserId(userId)
                .stream()
                .map(chapterProgressMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isChapterPassed(String chapterId, Long userId) {
        return chapterProgressRepo.existsByChapterIdAndUserIdAndCompletedTrue(chapterId, userId);
    }

    @Transactional(readOnly = true)
    public ChapterProgressResponseDto findById(ChapterProgressKey id) {
        return chapterProgressMapper.toResponseDto(
                chapterProgressRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình chương học"))
        );
    }

    @Transactional(readOnly = true)
    public List<ChapterProgressResponseDto> getByUserIdAndCourseId(Long userId, String courseId) {
        return chapterProgressRepo.findByUserIdAndCourseId(userId, courseId)
                .stream()
                .map(chapterProgressMapper::toResponseDto)
                .toList();
    }

    public ChapterProgressResponseDto create(@Valid ChapterProgressRequestDto dto) {
        log.info("Tạo tiến trình chương học cho user {} và chapter {}", dto.userId(), dto.chapterId());

        Chapter chapter = chapterRepo.findById(dto.chapterId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));

        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        ChapterProgress progress = chapterProgressMapper.toEntity(dto, chapter, user);
        ChapterProgress saved = chapterProgressRepo.save(progress);

        return chapterProgressMapper.toResponseDto(saved);
    }

    public ChapterProgressResponseDto update(ChapterProgressKey id, @Valid ChapterProgressRequestDto dto) {
        log.info("Cập nhật tiến trình chương học {} cho user {}", id.getChapterId(), id.getUserId());

        ChapterProgress existing = chapterProgressRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình chương học"));

        existing.setCompleted(dto.completed());
        existing.setCompletedAt(dto.completed() ? LocalDateTime.now() : null);

        ChapterProgress saved = chapterProgressRepo.save(existing);
        return chapterProgressMapper.toResponseDto(saved);
    }

    public void delete(ChapterProgressKey id) {
        log.info("Xóa tiến trình chương học {} của user {}", id.getChapterId(), id.getUserId());
        chapterProgressRepo.deleteById(id);
    }
}
