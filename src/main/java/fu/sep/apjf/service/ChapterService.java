package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.ChapterResponseDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.mapper.ChapterMapper;
import fu.sep.apjf.repository.ChapterRepository;
import fu.sep.apjf.repository.CourseRepository;
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
public class ChapterService {

    private final ChapterRepository chapterRepo;
    private final CourseRepository courseRepo;
    private final ApprovalRequestService approvalRequestService;

    /* ---------- READ ---------- */

    @Transactional(readOnly = true)
    public List<ChapterResponseDto> findAll() {
        return chapterRepo.findAll().stream()
                .map(ChapterMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChapterResponseDto> findByCourseId(String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        return chapterRepo.findByCourse(course).stream()
                .map(ChapterMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChapterResponseDto findById(String id) {
        return ChapterMapper.toResponseDto(chapterRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học")));
    }

    /* ---------- CREATE ---------- */
    public ChapterResponseDto create(@Valid ChapterRequestDto dto, Long staffId) {
        log.info("Nhân viên {} tạo chương học mới với mã: {}", staffId, dto.id());

        Course parent = courseRepo.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        if (chapterRepo.existsById(dto.id()))
            throw new IllegalArgumentException("Mã chương học đã tồn tại");

        Chapter ch = Chapter.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .status(EnumClass.Status.DRAFT) // Set as DRAFT until approved
                .course(parent)
                .build();

        // Set prerequisite chapter if provided
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học tiên quyết"));
            ch.setPrerequisiteChapter(prerequisite);
        }

        Chapter savedChapter = chapterRepo.save(ch);

        // Auto-create approval request for this new chapter
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                savedChapter.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo chương học {} và yêu cầu phê duyệt thành công", savedChapter.getId());
        return ChapterMapper.toResponseDto(savedChapter);
    }

    /* ---------- UPDATE ---------- */
    public ChapterResponseDto update(String currentId, @Valid ChapterRequestDto dto, Long staffId) {
        log.info("Nhân viên {} cập nhật chương học với mã: {}", staffId, currentId);

        Chapter chapter = chapterRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));

        chapter.setTitle(dto.title());
        chapter.setDescription(dto.description());
        chapter.setStatus(EnumClass.Status.DRAFT); // Reset to DRAFT when updated

        // Update prerequisite chapter if provided
        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học tiên quyết"));
            chapter.setPrerequisiteChapter(prerequisite);
        } else {
            chapter.setPrerequisiteChapter(null);
        }

        Chapter updatedChapter = chapterRepo.save(chapter);

        // Auto-create approval request for this updated chapter
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                updatedChapter.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật chương học {} và yêu cầu phê duyệt thành công", updatedChapter.getId());
        return ChapterMapper.toResponseDto(updatedChapter);
    }

    /* ---------- DELETE ---------- */
    public void delete(String id) {
        log.info("Xóa chương học với mã: {}", id);

        Chapter chapter = chapterRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học"));

        chapterRepo.delete(chapter);

        log.info("Xóa chương học {} thành công", id);
    }
}
