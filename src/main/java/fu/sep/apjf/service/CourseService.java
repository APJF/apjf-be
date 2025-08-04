package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.CourseMapper;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ApprovalRequestService approvalRequestService;
    private final MinioService minioService;
    private final CourseMapper courseMapper; // Thêm injection

    @Transactional(readOnly = true)
    public List<CourseResponseDto> findAll() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::toDtoWithPresignedUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDto findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        return toDtoWithPresignedUrlWithExams(course);
    }

    public CourseResponseDto create(CourseRequestDto dto, Long staffId) {
        if (courseRepository.existsById(dto.id())) {
            throw new EntityExistsException("ID khóa học đã tồn tại");
        }

        Course course = courseMapper.toEntity(dto); // Sử dụng mapper thay vì manual

        if (!userRepository.existsById(staffId)) {
            throw new EntityNotFoundException("Không tìm thấy nhân viên");
        }

        Course savedCourse = courseRepository.save(course);

        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                savedCourse.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        return courseMapper.toDto(savedCourse);
    }

    public CourseResponseDto update(String currentId, CourseRequestDto dto, Long staffId) {
        // Kiểm tra course tồn tại
        if (!courseRepository.existsById(currentId)) {
            throw new EntityNotFoundException("Không tìm thấy khóa học");
        }

        // Sử dụng mapper để tạo course với thông tin mới
        Course updatedCourse = courseMapper.toEntity(dto);
        updatedCourse.setId(currentId); // Giữ nguyên ID
        updatedCourse.setStatus(EnumClass.Status.INACTIVE); // Reset to INACTIVE when updated

        // Set prerequisite course if provided
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepository.findById(dto.prerequisiteCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học tiên quyết"));
            updatedCourse.setPrerequisiteCourse(prerequisite);
        }

        Course savedCourse = courseRepository.save(updatedCourse);
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                savedCourse.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        return courseMapper.toDto(savedCourse);
    }

    public String uploadCourseImage(MultipartFile file) throws Exception {
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép upload file ảnh (jpg, png, gif, etc.)");
        }

        // Validate file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }

        // Upload to MinIO và trả về object name
        return minioService.uploadCourseImage(file);
    }

    // Helper method để map Course entity sang DTO với presigned URL (không có exams)
    private CourseResponseDto toDtoWithPresignedUrl(Course course) {
        CourseResponseDto dto = courseMapper.toDto(course);
        return convertImageToPresignedUrl(dto);
    }

    // Helper method để map Course entity sang DTO với presigned URL (có exams)
    private CourseResponseDto toDtoWithPresignedUrlWithExams(Course course) {
        CourseResponseDto dto = courseMapper.toDtoWithExams(course);
        return convertImageToPresignedUrl(dto);
    }

    // Helper method để chuyển đổi image object name thành presigned URL
    private CourseResponseDto convertImageToPresignedUrl(CourseResponseDto dto) {
        String imageUrl = dto.image();
        if (imageUrl != null && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            try {
                imageUrl = minioService.getCourseImageUrl(imageUrl);
            } catch (Exception e) {
                log.warn("Failed to generate presigned URL for course image {}: {}", dto.image(), e.getMessage());
                // Giữ nguyên object name nếu có lỗi
            }
        }

        // Tạo CourseResponseDto mới với image URL đã convert
        return new CourseResponseDto(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.duration(),
                dto.level(),
                imageUrl,
                dto.requirement(),
                dto.status(),
                dto.prerequisiteCourseId(),
                dto.topics(),
                dto.exams(),
                dto.averageRating()
        );
    }
}
