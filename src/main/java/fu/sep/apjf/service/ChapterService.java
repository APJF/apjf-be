package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ChapterMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChapterService {

    public static final String CHAPTER_NOT_FOUND = "Không tìm thấy chương học";

    private final ChapterRepository chapterRepo;
    private final CourseRepository courseRepo;
    private final ApprovalRequestService approvalRequestService;
    private final UserRepository userRepository;
    private final ChapterMapper chapterMapper; // Thêm injection
    private final UnitProgressRepository unitProgressRepository;
    private final UnitRepository unitRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final CourseService courseService;

    @Transactional(readOnly = true)
    public List<ChapterResponseDto> findAll() {
        return chapterRepo.findAll().stream()
                .map(chapterMapper::toDto) // Sử dụng injected mapper với method mới
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChapterResponseDto> findByCourseId(String courseId) {
        List<Chapter> chapters = chapterRepo.findByCourseId(courseId);

        // Lazy validation: chỉ check course existence nếu list rỗng để tối ưu queries
        if (chapters.isEmpty() && !courseRepo.existsById(courseId)) {
            throw new EntityNotFoundException("Không tìm thấy course với ID: " + courseId);
        }

        return chapters.stream()
                .map(chapterMapper::toDto) // Sử dụng injected mapper
                .toList();
    }

    @Transactional(readOnly = true)
    public ChapterProgressResponseDto findById(String id, Long userId) {
        Chapter chapter = chapterRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chapter với ID: " + id));

        long totalUnits = unitRepository.countUnitsByChapterId(chapter.getId());
        long completedUnits = unitRepository.countCompletedUnitsByUserAndChapter(userId, chapter.getId());

        float percent = totalUnits == 0 ? 0 : ((float) completedUnits / totalUnits) * 100;
        boolean isCompleted = chapterRepo.isChapterCompleted(chapter.getId(), userId);

        return new ChapterProgressResponseDto(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getDescription(),
                chapter.getStatus(),
                chapter.getCourse().getId(),
                chapter.getPrerequisiteChapter() != null ? chapter.getPrerequisiteChapter().getId() : null,
                isCompleted,
                percent
        );
    }

    public ChapterResponseDto create(@Valid ChapterRequestDto dto, Long staffId) {
        log.info("Nhân viên {} tạo chương học mới với mã: {}", staffId, dto.id());

        Course parent = courseRepo.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        if (chapterRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Mã chương học đã tồn tại");

        // Sử dụng mapper để tạo entity
        Chapter ch = chapterMapper.toEntity(dto);
        ch.setCourse(parent);
        ch.setStatus(EnumClass.Status.INACTIVE);

        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học tiên quyết"));
            ch.setPrerequisiteChapter(prerequisite);
        }

        Chapter savedChapter = chapterRepo.save(ch);

        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                savedChapter.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo chương học {} và yêu cầu phê duyệt thành công", savedChapter.getId());
        return chapterMapper.toDto(savedChapter); // Sử dụng injected mapper
    }

    @Transactional(readOnly = true)
    public ChapterDetailWithProgressResponseDto getChapterDetailWithProgress(User user, String chapterId) {
        // Lấy chapter
        Chapter chapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương"));

        // Lấy toàn bộ progress của user cho chapter này
        List<UnitProgress> progresses = unitProgressRepository.findByUserAndChapter(user, chapterId);
        Map<String, Boolean> progressMap = progresses.stream()
                .collect(Collectors.toMap(p -> p.getUnit().getId(), UnitProgress::isCompleted));

        // Convert sang DTO và set isComplete
        List<UnitDetailWithExamResponseDto> unitDtos = chapter.getUnits().stream()
                .map(unit -> new UnitDetailWithExamResponseDto(
                        unit.getId(),
                        unit.getTitle(),
                        unit.getDescription(),
                        unit.getStatus(),
                        unit.getChapter().getId(),
                        unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null,
                        progressMap.getOrDefault(unit.getId(), false), // isComplete
                        unit.getMaterials().stream()
                                .map(m -> new MaterialResponseDto(
                                        m.getId(),
                                        m.getFileUrl(),
                                        m.getType(),
                                        m.getScript(),
                                        m.getTranslation()
                                ))
                                .toList(),
                        unit.getExams().stream()
                                .map(exam -> new ExamOverviewResponseDto(
                                        exam.getId(),
                                        exam.getTitle(),
                                        exam.getDescription(),
                                        exam.getDuration(),
                                        exam.getQuestions() != null ? exam.getQuestions().size() : 0,
                                        exam.getType()
                                ))
                                .toList()
                ))
                .toList();

        return new ChapterDetailWithProgressResponseDto(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getDescription(),
                chapter.getStatus(),
                chapter.getCourse().getId(),
                chapter.getPrerequisiteChapter() != null ? chapter.getPrerequisiteChapter().getId() : null,
                unitDtos
        );
    }

    public ChapterResponseDto update(String currentId, @Valid ChapterRequestDto dto, Long staffId) {
        log.info("Nhân viên {} cập nhật chương học với mã: {}", staffId, currentId);

        Chapter existingChapter = chapterRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException(CHAPTER_NOT_FOUND));

        // Cập nhật các trường của Chapter hiện có thay vì tạo mới
        existingChapter.setTitle(dto.title());
        existingChapter.setDescription(dto.description());
        existingChapter.setStatus(EnumClass.Status.INACTIVE); // Reset to INACTIVE when updated

        // Cập nhật course nếu khác
        if (!existingChapter.getCourse().getId().equals(dto.courseId())) {
            Course newCourse = courseRepo.findById(dto.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));
            existingChapter.setCourse(newCourse);
        }

        // Cập nhật prerequisite chapter
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học tiên quyết"));
            existingChapter.setPrerequisiteChapter(prerequisite);
        } else {
            existingChapter.setPrerequisiteChapter(null);
        }

        Chapter savedChapter = chapterRepo.save(existingChapter);

        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                savedChapter.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật chương học {} và yêu cầu phê duyệt thành công", savedChapter.getId());
        return chapterMapper.toDto(savedChapter);
    }

    public ChapterResponseDto deactivate(String chapterId, Long staffId) {
        // Check role STAFF
        if (!userRepository.existsById(staffId)) {
            throw new EntityNotFoundException("Không tìm thấy nhân viên");
        }

        // Tìm chapter
        Chapter existingChapter = chapterRepo.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));

        // Nếu đã INACTIVE thì không cần đổi nữa
        if (existingChapter.getStatus() == EnumClass.Status.INACTIVE) {
            throw new IllegalStateException("Chương học đã ở trạng thái INACTIVE");
        }

        // Cập nhật status thành INACTIVE
        existingChapter.setStatus(EnumClass.Status.INACTIVE);

        Chapter savedChapter = chapterRepo.save(existingChapter);

        return chapterMapper.toDto(savedChapter);
    }

}
