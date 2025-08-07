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
                .map(course -> {
                    CourseResponseDto dto = courseMapper.toDto(course);
                    return convertImageUrl(dto);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDto findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        CourseResponseDto dto = courseMapper.toDtoWithExams(course);
        return convertImageUrl(dto);
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
        Course existingCourse = courseRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        // Cập nhật các trường của Course hiện có thay vì tạo mới
        existingCourse.setTitle(dto.title());
        existingCourse.setDescription(dto.description());
        existingCourse.setDuration(dto.duration());
        existingCourse.setLevel(dto.level());
        existingCourse.setImage(dto.image());
        existingCourse.setRequirement(dto.requirement());
        existingCourse.setStatus(EnumClass.Status.INACTIVE); // Reset to INACTIVE when updated

        // Cập nhật prerequisite course
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepository.findById(dto.prerequisiteCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học tiên quyết"));
            existingCourse.setPrerequisiteCourse(prerequisite);
        } else {
            existingCourse.setPrerequisiteCourse(null);
        }

        Course savedCourse = courseRepository.save(existingCourse);
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

    // Helper method để chuyển đổi image object name thành presigned URL
    private CourseResponseDto convertImageUrl(CourseResponseDto dto) {
        String imageUrl = dto.image();
        if (imageUrl != null && !imageUrl.trim().isEmpty() &&
            !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            try {
                imageUrl = minioService.getCourseImageUrl(imageUrl);
            } catch (Exception e) {
                log.warn("Failed to generate presigned URL for course image {}: {}", dto.image(), e.getMessage());
                // Giữ nguyên object name nếu có lỗi
            }
        }

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
