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

    public static final String CHAPTER_NOT_FOUND = "Không tìm thấy chương học";

    private final ChapterRepository chapterRepo;
    private final CourseRepository courseRepo;
    private final ApprovalRequestService approvalRequestService;
    private final ChapterMapper chapterMapper; // Thêm injection

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
    public ChapterResponseDto findById(String id) {
        Chapter chapter = chapterRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CHAPTER_NOT_FOUND));
        return chapterMapper.toDtoWithExams(chapter); // Load exams cho detail
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

    public ChapterResponseDto update(String currentId, @Valid ChapterRequestDto dto, Long staffId) {
        log.info("Nhân viên {} cập nhật chương học với mã: {}", staffId, currentId);

        Chapter chapter = chapterRepo.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException(CHAPTER_NOT_FOUND));

        chapter.setTitle(dto.title());
        chapter.setDescription(dto.description());
        chapter.setStatus(EnumClass.Status.INACTIVE);

        if (dto.prerequisiteChapterId() != null) {
            Chapter prerequisite = chapterRepo.findById(dto.prerequisiteChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học tiên quyết"));
            chapter.setPrerequisiteChapter(prerequisite);
        } else {
            chapter.setPrerequisiteChapter(null);
        }

        Chapter updatedChapter = chapterRepo.save(chapter);

        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                updatedChapter.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật chương học {} và yêu cầu phê duyệt thành công", updatedChapter.getId());
        return chapterMapper.toDto(updatedChapter); // Sử dụng injected mapper
    }
}
