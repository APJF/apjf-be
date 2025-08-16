package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.CourseMapper;
import fu.sep.apjf.mapper.ExamOverviewMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ApprovalRequestService approvalRequestService;
    private final MinioService minioService;
    private final CourseMapper courseMapper;
    private final ExamOverviewMapper examMapper;
    private final CourseProgressRepository courseProgressRepository;
    private final UnitProgressRepository unitProgressRepository;
    private final ChapterRepository chapterRepository;
    private final UnitRepository unitRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    @Transactional(readOnly = true)
    public List<CourseResponseDto> findAll() {
        List<Course> courses = courseRepository.findAllCoursesWithTopics();

        // Lấy average rating cho tất cả courses chỉ 1 query
        Map<String, Float> averageRatings = reviewRepository.findAverageRatingForCourses(
                courses.stream().map(Course::getId).toList()
        ).stream().collect(Collectors.toMap(
                r -> r[0].toString(),                // courseId
                r -> ((Number) r[1]).floatValue()   // average rating
        ));

        // Lấy tất cả image object names và generate presigned URLs batch
        List<String> imageObjectNames = courses.stream()
                .map(Course::getImage)
                .filter(image -> image != null && !image.trim().isEmpty() &&
                        !image.startsWith("http://") && !image.startsWith("https://"))
                .toList();

        Map<String, String> presignedImageUrls = minioService.getCourseImageUrls(imageObjectNames);

        return courses.stream().map(course -> {
            Set<TopicDto> topicDtos = course.getTopics().stream()
                    .map(t -> new TopicDto(t.getId(), t.getName()))
                    .collect(Collectors.toSet());

            Float avgRating = averageRatings.getOrDefault(course.getId(), 0f);

            // Kiểm tra và sử dụng presigned URL nếu có
            String imageUrl = course.getImage();
            if (imageUrl != null && !imageUrl.trim().isEmpty() &&
                !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                imageUrl = presignedImageUrls.getOrDefault(imageUrl, imageUrl);
            }

            return new CourseResponseDto(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getDuration(),
                    course.getLevel(),
                    imageUrl,  // Sử dụng presigned URL thay vì raw object name
                    course.getRequirement(),
                    course.getStatus(),
                    course.getPrerequisiteCourse() != null ? course.getPrerequisiteCourse().getId() : null,
                    topicDtos,
                    avgRating,
                    false
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseDetailResponseDto> getAllByUser(User user) {
        // Lấy tất cả progress của user (đã enroll course nào thì mới có record trong CourseProgress)
        List<CourseProgress> progresses = courseProgressRepository.findByUser(user);

        // Lấy courseId list từ progresses
        List<String> courseIds = progresses.stream()
                .map(cp -> cp.getCourse().getId())
                .toList();

        // Lấy courses + topics bằng batch query
        List<Course> courses = courseRepository.findAllCoursesWithTopics();

        // Lấy average rating cho tất cả courses
        Map<String, Float> averageRatings = reviewRepository.findAverageRatingForCourses(courseIds)
                .stream().collect(Collectors.toMap(
                        r -> r[0].toString(),
                        r -> ((Number) r[1]).floatValue()
                ));

        // Lấy image objectNames để tạo presigned url
        List<String> imageObjectNames = courses.stream()
                .map(Course::getImage)
                .filter(img -> img != null && !img.trim().isEmpty()
                        && !img.startsWith("http://")
                        && !img.startsWith("https://"))
                .toList();
        Map<String, String> presignedImageUrls = minioService.getCourseImageUrls(imageObjectNames);

        // Map course -> dto
        return courses.stream().map(course -> {
            Set<TopicDto> topicDtos = course.getTopics().stream()
                    .map(t -> new TopicDto(t.getId(), t.getName()))
                    .collect(Collectors.toSet());

            Float avgRating = averageRatings.getOrDefault(course.getId(), 0f);

            String imageUrl = course.getImage();
            if (imageUrl != null && !imageUrl.trim().isEmpty()
                    && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                imageUrl = presignedImageUrls.getOrDefault(imageUrl, imageUrl);
            }

            // Tìm progress tương ứng
            CourseProgress progress = progresses.stream()
                    .filter(cp -> cp.getCourse().getId().equals(course.getId()))
                    .findFirst()
                    .orElse(null);

            CourseProgressResponseDto progressDto = null;
            if (progress != null) {
                progressDto = new CourseProgressResponseDto(
                        progress.isCompleted(),
                        calculateCourseProgress(user, course.getId())
                );
            }

            return new CourseDetailResponseDto(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getDuration(),
                    course.getLevel(),
                    imageUrl,
                    course.getRequirement(),
                    course.getStatus(),
                    course.getPrerequisiteCourse() != null ? course.getPrerequisiteCourse().getId() : null,
                    topicDtos,
                    avgRating,
                    progressDto
            );
        }).toList();
    }


    @Transactional
    public CourseDetailProgressResponseDto enrollCourse(User user, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        // 1. Tạo CourseProgress
        CourseProgress progress = courseProgressRepository.findByUserAndCourseId(user, courseId)
                .orElseGet(() -> courseProgressRepository.save(
                        CourseProgress.builder()
                                .user(user)
                                .course(course)
                                .completed(false)
                                .build()
                ));

        // 2. Lấy danh sách chapters của course
        List<Chapter> chapters = chapterRepository.findByCourseId(courseId);

        // 3. Tạo ChapterProgress cho từng chapter (nếu chưa có)
        List<ChapterProgress> chapterProgressList = new ArrayList<>();
        for (Chapter chapter : chapters) {
            if (!chapterProgressRepository.existsByUserAndChapter(user, chapter)) {
                chapterProgressList.add(
                        ChapterProgress.builder()
                                .id(new ChapterProgressKey(chapter.getId(), user.getId()))
                                .user(user)
                                .chapter(chapter)
                                .completed(false)
                                .build()
                );
            }
        }
        if (!chapterProgressList.isEmpty()) {
            chapterProgressRepository.saveAll(chapterProgressList);
        }

        // 4. Lấy tất cả units thuộc chapters
        List<String> chapterIds = chapters.stream().map(Chapter::getId).toList();
        List<Unit> units = unitRepository.findByChapterIdIn(chapterIds);

        // 5. Tạo UnitProgress cho từng unit (nếu chưa có)
        List<UnitProgress> unitProgressList = new ArrayList<>();
        for (Unit unit : units) {
            if (!unitProgressRepository.existsByUserAndUnit(user, unit)) {
                unitProgressList.add(
                        UnitProgress.builder()
                                .id(new UnitProgressKey(unit.getId(), user.getId()))
                                .user(user)
                                .unit(unit)
                                .completed(false)
                                .build()
                );
            }
        }
        if (!unitProgressList.isEmpty()) {
            unitProgressRepository.saveAll(unitProgressList);
        }

        return new CourseDetailProgressResponseDto(
                course.getId(),
                course.getTitle(),
                progress.isCompleted(),
                progress.getCompletedAt()
        );
    }

    @Transactional(readOnly = true)
    public CourseResponseDto findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));

        Float averageRating = reviewRepository.calculateAverageRatingByCourseId(id)
                .map(this::roundToHalfStar)
                .orElse(null);

        // Kiểm tra và generate presigned URL nếu cần
        String imageUrl = course.getImage();
        if (imageUrl != null && !imageUrl.trim().isEmpty() &&
            !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            try {
                imageUrl = minioService.getCourseImageUrl(imageUrl);
            } catch (Exception e) {
                log.warn("Failed to generate presigned URL for course image {}: {}", imageUrl, e.getMessage());
                // Giữ nguyên object name nếu có lỗi
            }
        }

        // Sử dụng mapper với presigned URL
        return courseMapper.toDetailDtoWithPresignedUrl(course, averageRating, imageUrl);
    }

    @Transactional(readOnly = true)
    public List<ExamOverviewResponseDto> getExamsByCourseId(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        return course.getExams().stream()
                .map(examMapper::toDto)
                .toList();
    }

    public CourseResponseDto create(CourseRequestDto dto, Long staffId) {
        if (courseRepository.existsById(dto.id())) {
            throw new EntityExistsException("ID khóa học đã tồn tại");
        }

        Course course = courseMapper.toEntity(dto);

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

        // Thêm averageRating parameter (course mới tạo chưa có rating)
        return courseMapper.toDto(savedCourse, null);
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

        // Thêm averageRating parameter
        Float avgRating = reviewRepository.calculateAverageRatingByCourseId(savedCourse.getId())
                .map(this::roundToHalfStar)
                .orElse(null);
        return courseMapper.toDto(savedCourse, avgRating);
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

    /**
     * Làm tròn rating về các mốc 0.5 (0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5)
     */
    private Float roundToHalfStar(Float rating) {
        if (rating == null) {
            return null;
        }
        // Nhân 2, làm tròn, rồi chia 2 để có các mốc 0.5
        return Math.round(rating * 2.0f) / 2.0f;
    }

    public float calculateCourseProgress(User user, String courseId) {
        long totalUnits = unitProgressRepository.countUnitsByCourseId(courseId);
        if (totalUnits == 0) return 0;

        long completedUnits = unitProgressRepository.countCompletedUnitsByUserAndCourse(user, courseId);

        return (completedUnits * 100.0f) / totalUnits;
    }



}
