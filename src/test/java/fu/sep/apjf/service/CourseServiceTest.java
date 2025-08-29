package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.*;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ApprovalRequestService approvalRequestService;
    @Mock
    private MinioService minioService;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private ExamOverviewMapper examMapper;
    @Mock
    private CourseProgressRepository courseProgressRepository;
    @Mock
    private UnitProgressRepository unitProgressRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private ChapterProgressRepository chapterProgressRepository;
    @Mock
    private ChapterMapper chapterMapper;
    @Mock
    private UnitMapper unitMapper;
    @Mock
    private MaterialMapper materialMapper;
    @Mock
    private CourseLearningPathRepository courseLearningPathRepository;
    @Mock
    private LearningPathService learningPathService;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course = new Course();
        course.setId("COURSE1");
        course.setTitle("Spring Boot");
        course.setStatus(EnumClass.Status.ACTIVE);
    }

    // ============================
    // create()
    // ============================
    @Test
    @DisplayName("Tạo course thành công")
    void testCreateSuccess() {
        CourseRequestDto dto = new CourseRequestDto(
                "COURSE1", "Spring Boot", "Mô tả", 10.0f,
                EnumClass.Level.N5, "image.png", "Yêu cầu", null,null,null
        );

        when(courseRepository.existsById("COURSE1")).thenReturn(false);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(courseMapper.toEntity(dto)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toDto(course, null)).thenReturn(
                new CourseResponseDto("COURSE1", "Spring Boot", "Mô tả", 10.0f,
                        EnumClass.Level.N5, "image.png", "Yêu cầu",
                        EnumClass.Status.ACTIVE, null, null,
                        0f, false, 0, null)
        );

        CourseResponseDto result = courseService.create(dto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("COURSE1");
        verify(approvalRequestService, times(1)).autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                "COURSE1",
                ApprovalRequest.RequestType.CREATE,
                1L
        );
    }

    @Test
    @DisplayName("Tạo course thất bại - ID đã tồn tại")
    void testCreateFail_IdExists() {
        CourseRequestDto dto = new CourseRequestDto("COURSE1", "Spring Boot", "desc", 10.0f,
                EnumClass.Level.N5, null, null, null,null,null);

        when(courseRepository.existsById("COURSE1")).thenReturn(true);

        assertThatThrownBy(() -> courseService.create(dto, 1L))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("ID khóa học đã tồn tại");
    }

    @Test
    @DisplayName("Tạo course thất bại - nhân viên không tồn tại")
    void testCreateFail_StaffNotFound() {
        CourseRequestDto dto = new CourseRequestDto("COURSE1", "Spring Boot", "desc", 10f,
                EnumClass.Level.N5, null, null, null,null,null);

        when(courseRepository.existsById("COURSE1")).thenReturn(false);
        when(courseMapper.toEntity(dto)).thenReturn(course);
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> courseService.create(dto, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Không tìm thấy nhân viên");
    }

    // ============================
    // deactivate()
    // ============================
    @Test
    @DisplayName("Deactivate course thành công")
    void testDeactivateSuccess() {
        when(courseRepository.findById("COURSE1")).thenReturn(Optional.of(course));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.save(course)).thenReturn(course);
        when(reviewRepository.calculateAverageRatingByCourseId("COURSE1")).thenReturn(Optional.of(4.0f));
        when(courseMapper.toDto(eq(course), any())).thenReturn(
                new CourseResponseDto("COURSE1", "Spring Boot", "Mô tả", 10f,
                        EnumClass.Level.N5, null, null,
                        EnumClass.Status.INACTIVE, null, null,
                        4.0f, false, 0, null)
        );

        CourseResponseDto result = courseService.deactivate("COURSE1", 1L);

        assertThat(result.status()).isEqualTo(EnumClass.Status.INACTIVE);
    }

    @Test
    @DisplayName("Deactivate course thất bại - không tìm thấy course")
    void testDeactivateFail_NotFound() {
        when(courseRepository.findById("COURSE1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deactivate("COURSE1", 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Không tìm thấy khóa học");
    }

    @Test
    @DisplayName("Deactivate course thất bại - course đã INACTIVE")
    void testDeactivateFail_AlreadyInactive() {
        course.setStatus(EnumClass.Status.INACTIVE);
        when(courseRepository.findById("COURSE1")).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.deactivate("COURSE1", 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Khóa học đã ở trạng thái INACTIVE");
    }

    // ============================
    // uploadCourseImage()
    // ============================
    @Test
    @DisplayName("Upload ảnh hợp lệ thành công")
    void testUploadCourseImageSuccess() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(minioService.uploadCourseImage(file)).thenReturn("objectName.png");

        String result = courseService.uploadCourseImage(file);

        assertThat(result).isEqualTo("objectName.png");
    }

    @Test
    @DisplayName("Upload ảnh thất bại - file không phải ảnh")
    void testUploadCourseImageFail_InvalidType() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");

        assertThatThrownBy(() -> courseService.uploadCourseImage(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chỉ cho phép upload file ảnh");
    }

    @Test
    @DisplayName("Upload ảnh thất bại - file quá lớn")
    void testUploadCourseImageFail_FileTooLarge() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB

        assertThatThrownBy(() -> courseService.uploadCourseImage(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kích thước file không được vượt quá 5MB");
    }
}
