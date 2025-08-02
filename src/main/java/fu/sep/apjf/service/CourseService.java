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
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public List<CourseResponseDto> findAll() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(CourseMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponseDto findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        return CourseMapper.toResponseDto(course);
    }

    public CourseResponseDto create(CourseRequestDto dto, Long staffId) {
        if (courseRepository.existsById(dto.id())) {
            throw new EntityExistsException("ID khóa học đã tồn tại");
        }

        Course course = CourseMapper.toEntity(dto);

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

        return CourseMapper.toResponseDto(savedCourse);
    }

    public CourseResponseDto update(String currentId, CourseRequestDto dto, Long staffId) {
        Course course = courseRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        // Cập nhật thông tin
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setDuration(dto.duration());
        course.setLevel(dto.level());
        course.setStatus(EnumClass.Status.INACTIVE);

        // Cập nhật prerequisite course
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepository.findById(dto.prerequisiteCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học tiên quyết"));
            course.setPrerequisiteCourse(prerequisite);
        } else {
            course.setPrerequisiteCourse(null);
        }

        Course updatedCourse = courseRepository.save(course);
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                updatedCourse.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        return CourseMapper.toResponseDto(updatedCourse);
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
