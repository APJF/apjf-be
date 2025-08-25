package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ReviewRequestDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.dto.response.ReviewResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.CourseMapper;
import fu.sep.apjf.mapper.ReviewMapper;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.ReviewRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private static final String COURSE_NOT_FOUND = "Không tìm thấy khóa học";

    private final ReviewRepository reviewRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final ReviewMapper reviewMapper;
    private final CourseMapper courseMapper;
    private final MinioService minioService;

    public ReviewResponseDto addReview(Long userId, ReviewRequestDto reviewRequestDto) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        Course course = courseRepo.findById(reviewRequestDto.courseId())
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND));

        if (reviewRepo.findByUserAndCourse(user, course).isPresent()) {
            throw new IllegalArgumentException("Người dùng đã đánh giá khóa học này");
        }

        Review review = reviewMapper.toEntity(
                reviewRequestDto,
                course,
                user
        );

        Review savedReview = reviewRepo.save(review);
        return mapReviewWithPresignedUrl(savedReview);
    }

    public List<ReviewResponseDto> getReviewsByCourse(String courseId) {
        if (!courseRepo.existsById(courseId)) {
            throw new EntityNotFoundException(COURSE_NOT_FOUND);
        }
        return reviewRepo.findByCourseId(courseId)
                .stream()
                .map(this::mapReviewWithPresignedUrl)
                .toList();
    }

    private ReviewResponseDto mapReviewWithPresignedUrl(Review review) {
        ReviewResponseDto dto = reviewMapper.toDto(review);

        // Generate presigned URL for user avatar
        String avatarUrl = null;
        try {
            if (review.getUser().getAvatar() != null) {
                avatarUrl = minioService.getAvatarUrl(review.getUser().getAvatar());
            }
        } catch (Exception e) {
            log.warn("Failed to generate presigned URL for avatar: {}", review.getUser().getAvatar(), e);
        }

        // Create new UserSummaryDto with presigned URL
        ReviewResponseDto.UserSummaryDto userWithPresignedUrl = new ReviewResponseDto.UserSummaryDto(
                dto.user().id(),
                dto.user().username(),
                avatarUrl
        );

        return new ReviewResponseDto(
                dto.id(),
                dto.courseId(),
                dto.rating(),
                dto.comment(),
                dto.createdAt(),
                userWithPresignedUrl
        );
    }

    public List<ReviewResponseDto> getReviewsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        return reviewRepo.findByUser(user)
                .stream()
                .map(this::mapReviewWithPresignedUrl)
                .toList();
    }

    public ReviewResponseDto updateReview(Long reviewId, Long userId,
                                          ReviewRequestDto reviewRequestDto) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("Không thể sửa đánh giá của người khác");
        }

        review.setRating(reviewRequestDto.rating());
        review.setComment(reviewRequestDto.comment());
        // lastUpdatedAt sẽ được Hibernate tự cập nhật nếu dùng @UpdateTimestamp
        Review updatedReview = reviewRepo.save(review);

        return mapReviewWithPresignedUrl(updatedReview);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("Không thể xóa đánh giá của người khác");
        }

        reviewRepo.delete(review);
    }

    public Float getAverageRating(String courseId) {
        return reviewRepo.calculateAverageRatingByCourseId(courseId).orElse(null);
    }

    public List<CourseResponseDto> getTopRatedCourses() {
        List<Course> topCourses = reviewRepo.findTop3RatedCourses();
        return topCourses.stream().map(course -> {
            Float avgRating = getAverageRating(course.getId());
            if (avgRating != null) {
                avgRating = Math.round(avgRating * 2.0f) / 2.0f;
            }
            String presignedImageUrl = null;
            try {
                presignedImageUrl = minioService.getCourseImageUrl(course.getImage());
            } catch (Exception e) {
                log.warn("Failed to generate presigned URL for course image {}: {}", course.getImage(), e.getMessage());
            }
            // Manually build CourseResponseDto with presigned image URL
            return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                presignedImageUrl,
                course.getRequirement(),
                course.getStatus(),
                course.getPrerequisiteCourse() != null ? course.getPrerequisiteCourse().getId() : null,
                course.getTopics() != null ? course.getTopics().stream().map(t -> new fu.sep.apjf.dto.request.TopicDto(t.getId(), t.getName())).collect(java.util.stream.Collectors.toSet()) : java.util.Collections.emptySet(),
                avgRating,
                false, // isEnrolled, not available in this context
                0 // totalStudent, default to 0 or fetch actual value if available
            );
        }).toList();
    }

}
