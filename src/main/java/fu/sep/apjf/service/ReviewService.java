package fu.sep.apjf.service;

import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.CourseMapper;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.ReviewRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private static final String COURSE_NOT_FOUND = "Không tìm thấy khóa học";

    public CourseResponseDto addReview(Long userId, String courseId, @Min(1) @Max(5) int rating, String comment) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND));

        if (reviewRepo.findByUserAndCourse(user, course).isPresent()) {
            throw new IllegalArgumentException("Người dùng đã đánh giá khóa học này");
        }

        Review review = Review.builder()
                .course(course)
                .user(user)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        reviewRepo.save(review);

        Course updatedCourse = courseRepo.findById(courseId).orElseThrow();
        return CourseMapper.toResponseDto(updatedCourse, getAverageRating(courseId));
    }

    public List<CourseResponseDto> getReviewsByCourse(String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND));

        return reviewRepo.findByCourse(course)
                .stream()
                .map(review -> CourseMapper.toResponseDto(review.getCourse(), getAverageRating(courseId)))
                .toList();
    }

    public List<CourseResponseDto> getReviewsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        return reviewRepo.findByUser(user)
                .stream()
                .map(review -> CourseMapper.toResponseDto(review.getCourse(), getAverageRating(review.getCourse().getId())))
                .toList();
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("Không thể xóa đánh giá của người khác");
        }

        reviewRepo.delete(review);
    }

    public double getAverageRating(String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND));

        return reviewRepo.calculateAverageRatingByCourse(course).orElse(0.0);
    }

    public List<CourseResponseDto> getTopRatedCourses(int topN) {
        List<Object[]> result = reviewRepo.findTopRatedCourses(PageRequest.of(0, topN));
        List<CourseResponseDto> topCourses = new ArrayList<>();

        for (Object[] obj : result) {
            Course course = (Course) obj[0];
            Double avgRating = (Double) obj[1];
            topCourses.add(CourseMapper.toResponseDto(course, avgRating));
        }

        return topCourses;
    }

}
