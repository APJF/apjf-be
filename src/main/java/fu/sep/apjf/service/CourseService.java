package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.*;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.exception.ResourceNotFoundException;
import fu.sep.apjf.mapper.*;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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
    private final ChapterMapper chapterMapper;
    private final UnitMapper unitMapper;
    private final MaterialMapper materialMapper;

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

        Map<String, Integer> totalStudents = courseProgressRepository
                .countTotalStudentsForCourses(courses.stream().map(Course::getId).toList())
                .stream()
                .collect(Collectors.toMap(
                        r -> r[0].toString(),
                        r -> ((Number) r[1]).intValue()
                ));

        // Lấy tất cả image object names và generate presigned URLs batch
        List<String> imageObjectNames = courses.stream()
                .map(Course::getImage)
                .filter(image -> image != null && !image.trim().isEmpty() &&
                        !image.startsWith("http://") && !image.startsWith("https://"))
                .toList();

        Map<String, String> presignedImageUrls = minioService.getCourseImageUrls(imageObjectNames);

        // Map thứ tự Level N5 -> N1
        Map<EnumClass.Level, Integer> levelOrder = Map.of(
                EnumClass.Level.N5, 1,
                EnumClass.Level.N4, 2,
                EnumClass.Level.N3, 3,
                EnumClass.Level.N2, 4,
                EnumClass.Level.N1, 5
        );

        // Tạo map ID -> Course để dễ truy cập
        Map<String, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // Đồ thị phụ thuộc và in-degree
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        for (Course c : courses) {
            graph.putIfAbsent(c.getId(), new ArrayList<>());
            inDegree.putIfAbsent(c.getId(), 0);

            if (c.getPrerequisiteCourse() != null) {
                String preId = c.getPrerequisiteCourse().getId();
                graph.putIfAbsent(preId, new ArrayList<>());
                graph.get(preId).add(c.getId()); // pre → current
                inDegree.put(c.getId(), inDegree.getOrDefault(c.getId(), 0) + 1);
                inDegree.putIfAbsent(preId, 0);
            }
        }

        // Hàng đợi cho Topological Sort, ưu tiên Level thấp hơn trước
        PriorityQueue<String> queue = new PriorityQueue<>((id1, id2) -> {
            Course c1 = courseMap.get(id1);
            Course c2 = courseMap.get(id2);
            int cmp = Integer.compare(
                    levelOrder.getOrDefault(c1.getLevel(), Integer.MAX_VALUE),
                    levelOrder.getOrDefault(c2.getLevel(), Integer.MAX_VALUE)
            );
            return cmp != 0 ? cmp : c1.getTitle().compareTo(c2.getTitle());
        });

        // Thêm các course có in-degree = 0 vào queue
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        // Thực hiện topological sort
        List<Course> sortedCourses = new ArrayList<>();
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            sortedCourses.add(courseMap.get(currentId));

            for (String next : graph.getOrDefault(currentId, List.of())) {
                inDegree.put(next, inDegree.get(next) - 1);
                if (inDegree.get(next) == 0) {
                    queue.offer(next);
                }
            }
        }

        // Convert sang DTO
        return sortedCourses.stream().map(course -> {
            Set<TopicDto> topicDtos = course.getTopics().stream()
                    .map(t -> new TopicDto(t.getId(), t.getName()))
                    .collect(Collectors.toSet());

            Float avgRating = averageRatings.getOrDefault(course.getId(), 0f);
            int students = totalStudents.getOrDefault(course.getId(), 0);


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
                    imageUrl,
                    course.getRequirement(),
                    course.getStatus(),
                    course.getPrerequisiteCourse() != null ? course.getPrerequisiteCourse().getId() : null,
                    topicDtos,
                    avgRating,
                    false,
                    students,
                    null
            );
        }).toList();
    }



    @Transactional(readOnly = true)
    public List<CourseDetailResponseDto> getAllByUser(User user) {
        // Lấy tất cả progress của user
        List<CourseProgress> progresses = courseProgressRepository.findByUserId(user.getId());

        // Lấy courseId list từ progresses
        List<String> courseIds = progresses.stream()
                .map(cp -> cp.getCourse().getId())
                .toList();

        if (courseIds.isEmpty()) {
            return List.of(); // Nếu chưa enroll khóa học nào thì trả về list rỗng
        }

        // Chỉ lấy các course mà user đã enroll
        List<Course> courses = courseRepository.findCoursesWithTopicsByIds(courseIds);

        // Lấy average rating cho những courses này
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

    @Transactional(readOnly = true)
    public CourseDetailWithStructureResponseDto getCourseDetail(User user, String id) {
        // 1. Lấy course + chapters + units + materials + topics
        Course course = courseRepository.findCourseWithStructureById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));

        // 2. Tính điểm đánh giá trung bình
        Float averageRating = reviewRepository.calculateAverageRatingByCourseId(id)
                .map(this::roundToHalfStar)
                .orElse(null);

        // 3. Đếm số học viên đăng ký
        int totalEnrolled = courseProgressRepository.countTotalStudentsByCourseId(course.getId());

        // 4. Generate presigned URL cho ảnh khóa học nếu cần
        String imageUrl = course.getImage();
        if (imageUrl != null && !imageUrl.trim().isEmpty() &&
                !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            try {
                imageUrl = minioService.getCourseImageUrl(imageUrl);
            } catch (Exception e) {
                log.warn("Failed to generate presigned URL for course image {}: {}", imageUrl, e.getMessage());
            }
        }

        Set<TopicDto> topicDtos = course.getTopics().stream()
                .map(t -> new TopicDto(t.getId(), t.getName()))
                .collect(Collectors.toSet());

        // 5. Mapping sang DTO
        List<ChapterDetailResponseDto> chapterDtos = course.getChapters().stream()
                .map(chapter -> {
                    List<UnitDetailResponseDto> unitDtos = chapter.getUnits().stream()
                            .map(unit -> {
                                List<MaterialResponseDto> materialDtos = unit.getMaterials().stream()
                                        .map(materialMapper::toDto)
                                        .toList();
                                return unitMapper.toDetailDto(unit, materialDtos);
                            })
                            .toList();
                    return chapterMapper.toDetailDto(chapter, unitDtos);
                })
                .toList();

        boolean isEnrolled = courseProgressRepository.existsByUserAndCourseId(user, id);

        return new CourseDetailWithStructureResponseDto(
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
                averageRating,
                isEnrolled,
                totalEnrolled,
                chapterDtos
        );
    }

    @Transactional
    public CourseDetailProgressResponseDto enrollCourse(User user, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        Optional<CourseProgress> courseProgress= courseProgressRepository.findByUserAndCourseId(user, courseId);

        if (courseProgress.isPresent()) {
            throw new IllegalStateException("Bạn đã đăng ký khóa học này rồi!");
        }

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
    public CourseResponseDto findById(User user,String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        List<CourseProgress> progresses = courseProgressRepository.findByUserId(user.getId());

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

        Float averageRating = reviewRepository.calculateAverageRatingByCourseId(id)
                .map(this::roundToHalfStar)
                .orElse(null);

        int totalStudent = courseProgressRepository.countTotalStudentsByCourseId(course.getId());


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

        if(courseProgressRepository.existsByUserAndCourseId(user, id)){
            return courseMapper.toDetailDtoWithPresignedUrl(course, averageRating, imageUrl, true,totalStudent, progressDto);
        }

        // Sử dụng mapper với presigned URL
        return courseMapper.toDetailDtoWithPresignedUrl(course, averageRating, imageUrl, false,totalStudent,progressDto);
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

    public CourseResponseDto deactivate(String courseId, Long staffId) {
        // Kiểm tra khóa học có tồn tại không
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        // Nếu đã INACTIVE thì không cần đổi nữa
        if (existingCourse.getStatus() == EnumClass.Status.INACTIVE) {
            throw new IllegalStateException("Khóa học đã ở trạng thái INACTIVE");
        }

        if (!userRepository.existsById(staffId)) {
            throw new EntityNotFoundException("Không tìm thấy nhân viên");
        }
        // Cập nhật status thành INACTIVE
        existingCourse.setStatus(EnumClass.Status.INACTIVE);

        Course savedCourse = courseRepository.save(existingCourse);

        // Lấy averageRating để trả về DTO
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
        long totalChapters = unitProgressRepository.countChaptersByCourseId(courseId);
        if (totalChapters == 0) return 0;

        long completedChapters = unitProgressRepository.countCompletedChaptersByUserAndCourse(user, courseId);

        return (completedChapters * 100.0f) / totalChapters;
    }
}
