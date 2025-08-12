package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UnitProgressRequestDto;
import fu.sep.apjf.dto.response.UnitProgressResponseDto;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.UnitProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.UnitProgressMapper;
import fu.sep.apjf.repository.UnitProgressRepository;
import fu.sep.apjf.repository.UnitRepository;
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
public class UnitProgressService {

    private final UnitProgressRepository unitProgressRepo;
    private final UnitRepository unitRepo;
    private final UserRepository userRepo;
    private final UnitProgressMapper unitProgressMapper;

    /* ---------- READ ALL ---------- */
    @Transactional(readOnly = true)
    public List<UnitProgressResponseDto> list() {
        return unitProgressRepo.findAll()
                .stream()
                .map(unitProgressMapper::toDto)
                .toList();
    }

    /* ---------- FIND BY USER ---------- */
    @Transactional(readOnly = true)
    public List<UnitProgressResponseDto> findByUserId(Long userId) {
        List<UnitProgress> progresses = unitProgressRepo.findByUserId(userId);

        if (progresses.isEmpty() && !userRepo.existsById(userId)) {
            throw new EntityNotFoundException("Không tìm thấy user với ID: " + userId);
        }

        return progresses.stream()
                .map(unitProgressMapper::toDto)
                .toList();
    }

    /* ---------- FIND BY USER + CHAPTER ---------- */
    @Transactional(readOnly = true)
    public List<UnitProgressResponseDto> findByUserIdAndChapterId(Long userId, String chapterId) {
        return unitProgressRepo.findByUserIdAndUnit_ChapterId(userId, chapterId)
                .stream()
                .map(unitProgressMapper::toDto)
                .toList();
    }

    /* ---------- FIND BY USER + COURSE ---------- */
    @Transactional(readOnly = true)
    public List<UnitProgressResponseDto> findByUserIdAndCourseId(Long userId, String courseId) {
        return unitProgressRepo.findByUserIdAndUnit_Chapter_CourseId(userId, courseId)
                .stream()
                .map(unitProgressMapper::toDto)
                .toList();
    }

    /* ---------- CHECK COMPLETED ---------- */
    @Transactional(readOnly = true)
    public boolean hasPassedUnit(String unitId, Long userId) {
        return unitProgressRepo.existsByUnitIdAndUserIdAndCompletedTrue(unitId, userId);
    }

    /* ---------- CREATE ---------- */
    public UnitProgressResponseDto create(@Valid UnitProgressRequestDto dto) {
        log.info("User {} tạo progress cho Unit {}", dto.userId(), dto.unitId());

        Unit unit = unitRepo.findById(dto.unitId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy unit với ID: " + dto.unitId()));

        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user với ID: " + dto.userId()));

        UnitProgressKey key = new UnitProgressKey(dto.unitId(), dto.userId());

        if (unitProgressRepo.existsById(key)) {
            throw new IllegalArgumentException("Progress cho unit này đã tồn tại");
        }

        UnitProgress progress = UnitProgress.builder()
                .id(key)
                .unit(unit)
                .user(user)
                .completed(dto.completed())
                .completedAt(dto.completed() ? LocalDateTime.now() : null)
                .build();

        UnitProgress saved = unitProgressRepo.save(progress);
        log.info("Tạo progress thành công cho unit {} - user {}", dto.unitId(), dto.userId());

        return unitProgressMapper.toDto(saved);
    }

    /* ---------- UPDATE ---------- */
    public UnitProgressResponseDto update(UnitProgressKey key, @Valid UnitProgressRequestDto dto) {
        log.info("User {} cập nhật progress cho Unit {}", dto.userId(), dto.unitId());

        UnitProgress existing = unitProgressRepo.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy UnitProgress với key: " + key));

        existing.setCompleted(dto.completed());
        existing.setCompletedAt(dto.completed() ? LocalDateTime.now() : null);

        UnitProgress saved = unitProgressRepo.save(existing);
        log.info("Cập nhật progress thành công cho unit {} - user {}", dto.unitId(), dto.userId());

        return unitProgressMapper.toDto(saved);
    }

    /* ---------- DELETE ---------- */
    public void delete(UnitProgressKey key) {
        if (!unitProgressRepo.existsById(key)) {
            throw new EntityNotFoundException("Không tìm thấy UnitProgress với key: " + key);
        }
        unitProgressRepo.deleteById(key);
        log.info("Xóa progress với key: {}", key);
    }
}
