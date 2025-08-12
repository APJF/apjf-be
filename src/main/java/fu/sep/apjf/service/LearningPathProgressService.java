package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LearningPathProgressRequestDto;
import fu.sep.apjf.dto.response.LearningPathProgressDetailResponseDto;
import fu.sep.apjf.dto.response.LearningPathProgressOverviewDto;
import fu.sep.apjf.entity.LearningPath;
import fu.sep.apjf.entity.LearningPathProgress;
import fu.sep.apjf.entity.LearningPathProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.LearningPathProgressDetailMapper;
import fu.sep.apjf.mapper.LearningPathProgressOverviewMapper;
import fu.sep.apjf.repository.CourseLearningPathRepository;
import fu.sep.apjf.repository.LearningPathProgressRepository;
import fu.sep.apjf.repository.LearningPathRepository;
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
public class LearningPathProgressService {

    private final LearningPathProgressRepository learningPathProgressRepo;
    private final LearningPathRepository learningPathRepo;
    private final UserRepository userRepo;
    private final CourseLearningPathRepository courseLearningPathRepository;
    private final LearningPathProgressDetailMapper detailMapper;
    private final LearningPathProgressOverviewMapper overviewMapper;

    @Transactional(readOnly = true)
    public List<LearningPathProgressOverviewDto> getOverviewByUser(Long userId) {
        return learningPathProgressRepo.findByUserId(userId).stream()
                .map(entity -> {
                    int totalUnits = getTotalUnits(entity.getLearningPath().getId());
                    int completedUnits = getTotalCompletedUnits(entity.getLearningPath().getId());
                    double percentage = totalUnits == 0 ? 0 : (completedUnits * 100.0 / totalUnits);
                    return overviewMapper.toDto(entity, totalUnits, completedUnits, percentage);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public LearningPathProgressDetailResponseDto getDetail(LearningPathProgressKey id) {
        return detailMapper.toDetailResponseDto(
                learningPathProgressRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình learning path"))
        );
    }

    public LearningPathProgressDetailResponseDto create(@Valid LearningPathProgressRequestDto dto) {
        log.info("Tạo tiến trình learning path {} cho user {}", dto.learningPathId(), dto.userId());

        LearningPath learningPath = learningPathRepo.findById(dto.learningPathId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy learning path"));

        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        LearningPathProgress progress = detailMapper.toEntity(dto, learningPath, user);
        LearningPathProgress saved = learningPathProgressRepo.save(progress);

        return detailMapper.toDetailResponseDto(saved);
    }

    public LearningPathProgressDetailResponseDto update(LearningPathProgressKey id, @Valid LearningPathProgressRequestDto dto) {
        log.info("Cập nhật tiến trình learning path {} cho user {}", id.getLearningPathId(), id.getUserId());

        LearningPathProgress existing = learningPathProgressRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình learning path"));

        existing.setCompleted(dto.completed());
        existing.setCompletedAt(dto.completed() ? LocalDateTime.now() : null);

        LearningPathProgress saved = learningPathProgressRepo.save(existing);
        return detailMapper.toDetailResponseDto(saved);
    }

    public void delete(LearningPathProgressKey id) {
        log.info("Xóa tiến trình learning path {} của user {}", id.getLearningPathId(), id.getUserId());
        learningPathProgressRepo.deleteById(id);
    }

    // --- Dùng lại 2 hàm của bạn ---
    public int getTotalUnits(Long learningPathId) {
        return courseLearningPathRepository.findByLearningPathId(learningPathId).stream()
                .mapToInt(clp -> clp.getCourse().getChapters().stream()
                        .mapToInt(ch -> ch.getUnits().size())
                        .sum())
                .sum();
    }

    public int getTotalCompletedUnits(Long learningPathId) {
        return courseLearningPathRepository.findByLearningPathId(learningPathId).stream()
                .mapToInt(clp -> clp.getCourse().getChapters().stream()
                        .mapToInt(ch -> (int) ch.getUnits().stream()
                                .flatMap(unit -> unit.getUnitProgresses().stream())
                                .filter(up -> up.isCompleted())
                                .count())
                        .sum())
                .sum();
    }
}
