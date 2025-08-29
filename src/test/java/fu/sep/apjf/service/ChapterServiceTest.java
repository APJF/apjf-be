package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.ChapterDetailWithProgressResponseDto;
import fu.sep.apjf.dto.response.ChapterProgressResponseDto;
import fu.sep.apjf.dto.response.ChapterResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.ChapterMapper;
import fu.sep.apjf.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepo;
    @Mock
    private CourseRepository courseRepo;
    @Mock
    private ApprovalRequestService approvalRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChapterMapper chapterMapper;
    @Mock
    private UnitProgressRepository unitProgressRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private ChapterProgressRepository chapterProgressRepository;
    @Mock
    private CourseService courseService;

    @InjectMocks
    private ChapterService chapterService;

    private Chapter chapter;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course = new Course();
        course.setId("COURSE1");
        course.setTitle("Spring Boot Basics");

        chapter = new Chapter();
        chapter.setId("CHAP1");
        chapter.setTitle("Chương 1");
        chapter.setDescription("Mô tả chương 1");
        chapter.setStatus(EnumClass.Status.ACTIVE);
        chapter.setCourse(course);
    }

    // ============================
    // 1. findById + progress
    // ============================
    @Test
    @DisplayName("Lấy ChapterProgressResponseDto thành công")
    void testFindByIdSuccess() {
        when(chapterRepo.findById("CHAP1")).thenReturn(Optional.of(chapter));
        when(unitRepository.countUnitsByChapterId("CHAP1")).thenReturn(10L);
        when(unitRepository.countCompletedUnitsByUserAndChapter(1L, "CHAP1")).thenReturn(5L);
        when(chapterRepo.isChapterCompleted("CHAP1", 1L)).thenReturn(false);

        ChapterProgressResponseDto result = chapterService.findById("CHAP1", 1L);

        assertThat(result).isNotNull();
        assertThat(result.percent()).isEqualTo(50.0f);
        assertThat(result.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("Lấy ChapterProgressResponseDto thất bại - không tìm thấy chapter")
    void testFindByIdNotFound() {
        when(chapterRepo.findById("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chapterService.findById("INVALID", 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Không tìm thấy chapter");
    }

    // ============================
    // 2. create chapter
    // ============================
    @Test
    @DisplayName("Tạo chapter mới thành công")
    void testCreateChapterSuccess() {
        // ✅ Đặt courseId trùng với course đã setup ở @BeforeEach
        ChapterRequestDto dto = new ChapterRequestDto(
                "CHAP2",
                "Tiêu đề",
                "Mô tả",
                EnumClass.Status.ACTIVE,
                "COURSE1",    // <-- Quan trọng: trùng với course.getId()
                null
        );

        // ✅ Mock courseRepo trả về course
        when(courseRepo.findById("COURSE1")).thenReturn(Optional.of(course));
        when(chapterRepo.existsById("CHAP2")).thenReturn(false);

        Chapter newChapter = new Chapter();
        newChapter.setId("CHAP2");
        newChapter.setTitle(dto.title());
        newChapter.setDescription(dto.description());
        newChapter.setCourse(course);

        when(chapterMapper.toEntity(dto)).thenReturn(newChapter);
        when(chapterRepo.save(any(Chapter.class))).thenReturn(newChapter);
        when(chapterMapper.toDto(newChapter)).thenReturn(
                new ChapterResponseDto("CHAP2", "Tiêu đề", "Mô tả",
                        EnumClass.Status.INACTIVE, "COURSE1", null)
        );

        ChapterResponseDto result = chapterService.create(dto, 5L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("CHAP2");
        verify(approvalRequestService, times(1)).autoCreateApprovalRequest(
                ApprovalRequest.TargetType.CHAPTER,
                "CHAP2",
                ApprovalRequest.RequestType.CREATE,
                5L
        );
    }

    // ============================
    // 3. deactivate chapter
    // ============================
    @Test
    @DisplayName("Deactivate chapter thành công")
    void testDeactivateChapterSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(chapterRepo.findById("CHAP1")).thenReturn(Optional.of(chapter));
        when(chapterRepo.save(any(Chapter.class))).thenReturn(chapter);
        when(chapterMapper.toDto(chapter)).thenReturn(
                new ChapterResponseDto("CHAP1", "Chương 1", "Mô tả chương 1", EnumClass.Status.INACTIVE, "COURSE1", null)
        );

        ChapterResponseDto result = chapterService.deactivate("CHAP1", 1L);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(EnumClass.Status.INACTIVE);
    }

    @Test
    @DisplayName("Deactivate chapter thất bại - nhân viên không tồn tại")
    void testDeactivateChapterStaffNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> chapterService.deactivate("CHAP1", 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Không tìm thấy nhân viên");
    }
}
