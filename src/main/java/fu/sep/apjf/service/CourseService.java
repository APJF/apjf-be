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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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
                .map(courseMapper::toDto) // Sử dụng injected mapper
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDto findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        return courseMapper.toDtoWithExams(course); // Sử dụng injected mapper
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

    public String uploadCourseImage(String courseId, MultipartFile file) throws Exception {
        // Validate course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId);
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép upload file ảnh (jpg, png, gif, etc.)");
        }

        // Validate file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB");
        }

        // Upload to MinIO
        return minioService.uploadCourseImage(file, courseId);
    }
}
