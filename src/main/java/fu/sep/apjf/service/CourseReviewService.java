package fu.sep.apjf.service;

import fu.sep.apjf.dto.CourseReviewDto;
import fu.sep.apjf.dto.CourseWithRatingDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseReview;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.CourseReviewMapper;
import fu.sep.apjf.mapper.CourseWithRatingMapper;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.CourseReviewRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;

    public CourseReviewDto addReview(Long userId, String courseId, @Min(1) @Max(5) int rating, String comment) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        if (courseReviewRepo.findByUserAndCourse(user, course).isPresent()) {
            throw new IllegalArgumentException("Người dùng đã đánh giá khóa học này");
        }

        CourseReview review = CourseReview.builder()
                .course(course)
                .user(user)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build();

        review = courseReviewRepo.save(review);
        return CourseReviewMapper.toDto(review);
    }

    public List<CourseReviewDto> getReviewsByCourse(String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        return courseReviewRepo.findByCourse(course)
                .stream()
                .map(CourseReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CourseReviewDto> getReviewsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        return courseReviewRepo.findByUser(user)
                .stream()
                .map(CourseReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId, Long userId) {
        CourseReview review = courseReviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("Không thể xóa đánh giá của người khác");
        }

        courseReviewRepo.delete(review);
    }

    // ✅ ĐÃ SỬA: Trả về danh sách CourseWithRatingDto
    public List<CourseWithRatingDto> getTopRatedCourses(int topN) {
        List<Object[]> result = courseReviewRepo.findTopRatedCourses(PageRequest.of(0, topN));
        List<CourseWithRatingDto> topCourses = new ArrayList<>();

        for (Object[] obj : result) {
            Course course = (Course) obj[0];
            Double avgRating = (Double) obj[1];
            topCourses.add(CourseWithRatingMapper.toDto(course, avgRating));
        }

        return topCourses;
    }

    public double getAverageRating(String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        return courseReviewRepo.calculateAverageRatingByCourse(course).orElse(0.0);
    }
}
