package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.request.CourseSearchFilter;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.CourseMapper;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.ReviewRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Khóa học (Course)
 * <p>
 * Bao gồm các chức năng cơ bản:
 * - Tìm kiếm: findById, findAll, findByStatus...
 * - Tạo mới: create
 * - Cập nhật: update
 * - Xóa: delete
 * - Xử lý nghiệp vụ: updateStatus
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ApprovalRequestService approvalRequestService;
    private final ReviewRepository reviewRepository;

    /* ---------- READ ---------- */

    /**
     * Lấy tất cả khóa học dạng list không phân trang
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDto> findAll() {
        List<Course> courses = courseRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return courses.stream()
                .map(CourseMapper::toResponseDto)
                .toList();
    }

    /**
     * Tìm tất cả khóa học với phân trang và sắp xếp
     * Method accepts search filters as a filter object to reduce parameter count
     */
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> findAll(int page, int size, String sortBy, String direction,
                                CourseSearchFilter filter) {
        // Tạo đối tượng Pageable cho phân trang và sắp xếp
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // Gọi phương thức tìm kiếm với các điều kiện lọc
        return findAllInternal(pageable, filter.title(), filter.level(), filter.status(), filter.entryOnly());
    }

    /**
     * Tìm tất cả khóa học với phân trang, sắp xếp và các điều kiện lọc
     * Private implementation to avoid direct 'this' calls to transactional methods
     */
    @Transactional(readOnly = true)
    private Page<CourseResponseDto> findAllInternal(Pageable pageable, String title, EnumClass.Level level,
                                EnumClass.Status status, Boolean entryOnly) {
        Page<Course> coursePage;

        // Xây dựng câu lệnh truy vấn dựa trên các tham số đầu vào
        if (title != null && !title.isEmpty() && level != null && status != null) {
            coursePage = courseRepository.findByTitleContainingIgnoreCaseAndLevelAndStatus(title, level, status, pageable);
        } else if (title != null && !title.isEmpty() && level != null) {
            coursePage = courseRepository.findByTitleContainingIgnoreCaseAndLevel(title, level, pageable);
        } else if (title != null && !title.isEmpty() && status != null) {
            coursePage = courseRepository.findByTitleContainingIgnoreCaseAndStatus(title, status, pageable);
        } else if (level != null && status != null) {
            coursePage = courseRepository.findByLevelAndStatus(level, status, pageable);
        } else if (title != null && !title.isEmpty()) {
            coursePage = courseRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (level != null) {
            coursePage = courseRepository.findByLevel(level, pageable);
        } else if (status != null) {
            coursePage = courseRepository.findByStatus(status, pageable);
        } else if (Boolean.TRUE.equals(entryOnly)) {
            coursePage = courseRepository.findByPrerequisiteCourseIsNull(pageable);
        } else {
            coursePage = courseRepository.findAll(pageable);
        }

        // Chuyển đổi từ Page<Course> sang Page<CourseResponseDto>
        return coursePage.map(CourseMapper::toResponseDto);
    }

    /**
     * Tìm khóa học theo ID
     */
    @Transactional(readOnly = true)
    public CourseResponseDto findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        return CourseMapper.toResponseDto(course);
    }

    /**
     * Tìm khóa học đã xuất bản theo cấp độ
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDto> findPublishedCoursesByLevel(EnumClass.Level level) {
        return courseRepository.findByLevelAndStatus(level, EnumClass.Status.PUBLISHED)
                .stream()
                .map(CourseMapper::toResponseDto)
                .toList();
    }

    /**
     * Tìm khóa học theo tên
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDto> findByTitle(String title) {
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(title);
        return courses.stream()
                .map(CourseMapper::toResponseDto)
                .toList();
    }

    /* ---------- CREATE ---------- */
    public CourseResponseDto create(@Valid CourseRequestDto dto, Long staffId) {
        log.info("Staff {} đang tạo khóa học mới: {}", staffId, dto.title());

        // Check trùng ID
        if (dto.id() != null && courseRepository.existsById(dto.id())) {
            throw new EntityExistsException("ID khóa học đã tồn tại");
        }

        // Chuyển đổi từ DTO sang Entity
        Course course = CourseMapper.toEntity(dto);

        // Kiểm tra staffId có tồn tại không
        if (!userRepository.existsById(staffId)) {
            throw new EntityNotFoundException("Không tìm thấy nhân viên");
        }

        // Lưu khóa học
        Course savedCourse = courseRepository.save(course);

        // Tự động tạo yêu cầu phê duyệt cho khóa học mới này
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                savedCourse.getId(),
                ApprovalRequest.RequestType.CREATE,
                staffId
        );

        log.info("Tạo khóa học {} và yêu cầu phê duyệt thành công", savedCourse.getId());
        return CourseMapper.toResponseDto(savedCourse);
    }

    /* ---------- UPDATE ---------- */
    public CourseResponseDto update(String currentId, @Valid CourseRequestDto dto, Long staffId) {
        log.info("Staff {} đang cập nhật khóa học: {}", staffId, currentId);

        // Kiểm tra course có tồn tại không
        Course course = courseRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        // Cập nhật thông tin
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        course.setImage(dto.image());
        course.setLevel(dto.level());
        course.setStatus(EnumClass.Status.DRAFT); // Reset trạng thái về DRAFT khi cập nhật

        // Cập nhật prerequisite course
        if (dto.prerequisiteCourseId() != null) {
            Course prerequisite = courseRepository.findById(dto.prerequisiteCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học tiên quyết"));
            course.setPrerequisiteCourse(prerequisite);
        } else {
            course.setPrerequisiteCourse(null);
        }

        Course updatedCourse = courseRepository.save(course);

        // Tự động tạo yêu cầu phê duyệt cho việc cập nhật khóa học
        approvalRequestService.autoCreateApprovalRequest(
                ApprovalRequest.TargetType.COURSE,
                updatedCourse.getId(),
                ApprovalRequest.RequestType.UPDATE,
                staffId
        );

        log.info("Cập nhật khóa học {} và yêu cầu phê duyệt thành công", updatedCourse.getId());
        return CourseMapper.toResponseDto(updatedCourse);
    }

    /**
     * Helper methods to enhance CourseResponseDto with rating information
     */

    @Transactional(readOnly = true)
    public CourseResponseDto findByIdWithRating(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));

        CourseResponseDto baseDto = CourseMapper.toResponseDto(course);
        Double averageRating = reviewRepository.calculateAverageRatingByCourse(course).orElse(0.0);

        // Create a new DTO with all the original fields plus the rating
        return new CourseResponseDto(
            baseDto.id(),
            baseDto.title(),
            baseDto.description(),
            baseDto.duration(),
            baseDto.level(),
            baseDto.image(),
            baseDto.requirement(),
            baseDto.status(),
            baseDto.prerequisiteCourseId(),
            baseDto.topics(),
            baseDto.exams(),
            averageRating
        );
    }

    /**
     * Method to enhance a list of CourseResponseDtos with ratings
     */
    @Transactional(readOnly = true)
    public List<CourseResponseDto> enhanceWithRatings(List<CourseResponseDto> dtos) {
        return dtos.stream().map(dto -> {
            Course course = courseRepository.findById(dto.id()).orElse(null);
            if (course == null) return dto;

            Double averageRating = reviewRepository.calculateAverageRatingByCourse(course).orElse(0.0);

            return new CourseResponseDto(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.duration(),
                dto.level(),
                dto.image(),
                dto.requirement(),
                dto.status(),
                dto.prerequisiteCourseId(),
                dto.topics(),
                dto.exams(),
                averageRating
            );
        }).toList();
    }
}
