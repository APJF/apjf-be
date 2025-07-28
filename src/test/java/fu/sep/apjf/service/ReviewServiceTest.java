package fu.sep.apjf.service;

import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.ReviewRepository;
import fu.sep.apjf.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    private ReviewRepository reviewRepo;
    private CourseRepository courseRepo;
    private UserRepository userRepo;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepo = mock(ReviewRepository.class);
        courseRepo = mock(CourseRepository.class);
        userRepo = mock(UserRepository.class);
        reviewService = new ReviewService(reviewRepo, courseRepo, userRepo);
    }

    @Test
    void testAddReview_Success() {
        Long userId = 1L;
        String courseId = "C001";
        int rating = 4;
        String comment = "Good";
        User user = new User();
        user.setId(userId);
        Course course = new Course();
        course.setId(courseId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(reviewRepo.findByUserAndCourse(user, course)).thenReturn(Optional.empty());
        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(reviewRepo.calculateAverageRatingByCourse(course)).thenReturn(Optional.of(4.0));
        CourseResponseDto result = reviewService.addReview(userId, courseId, rating, comment);
        assertNotNull(result);
        verify(reviewRepo).save(any(Review.class));
    }

    @Test
    void testAddReview_UserAlreadyReviewed() {
        Long userId = 1L;
        String courseId = "C001";
        User user = new User();
        Course course = new Course();
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(reviewRepo.findByUserAndCourse(user, course)).thenReturn(Optional.of(new Review()));
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(userId, courseId, 5, "Nice"));
    }

    @Test
    void testGetReviewsByCourse() {
        String courseId = "C001";
        Course course = new Course();
        course.setId(courseId);
        Review review = Review.builder().course(course).build();
        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(reviewRepo.findByCourse(course)).thenReturn(List.of(review));
        when(reviewRepo.calculateAverageRatingByCourse(course)).thenReturn(Optional.of(4.0));
        List<CourseResponseDto> result = reviewService.getReviewsByCourse(courseId);
        assertEquals(1, result.size());
    }

    @Test
    void testGetReviewsByUser() {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        Course course = new Course();
        course.setId("C002");
        Review review = Review.builder().course(course).user(user).build();
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepo.findByUser(user)).thenReturn(List.of(review));
        when(courseRepo.findById(course.getId())).thenReturn(Optional.of(course));
        when(reviewRepo.calculateAverageRatingByCourse(course)).thenReturn(Optional.of(5.0));
        List<CourseResponseDto> result = reviewService.getReviewsByUser(userId);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteReview_Success() {
        Long reviewId = 10L;
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        Review review = Review.builder().id(reviewId).user(user).build();
        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(review));
        reviewService.deleteReview(reviewId, userId);
        verify(reviewRepo).delete(review);
    }

    @Test
    void testDeleteReview_NotOwner_ShouldThrow() {
        Long reviewId = 10L;
        Long userId = 2L;
        User otherUser = new User();
        otherUser.setId(3L);
        Review review = Review.builder().id(reviewId).user(otherUser).build();
        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(review));
        assertThrows(SecurityException.class, () -> reviewService.deleteReview(reviewId, userId));
    }

    @Test
    void testGetTopRatedCourses() {
        Course course = new Course();
        course.setId("C003");
        course.setTitle("Test Course");
        course.setDescription("Desc");
        Double avgRating = 4.8;
        Object[] row = new Object[]{course, avgRating};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);
        when(reviewRepo.findTopRatedCourses(PageRequest.of(0, 5))).thenReturn(mockResult);
        List<CourseResponseDto> result = reviewService.getTopRatedCourses(5);
        assertEquals(1, result.size());
        assertEquals("C003", result.get(0).id());
        assertEquals(4.8, result.get(0).averageRating());
    }

    @Test
    void testGetAverageRating() {
        Course course = new Course();
        course.setId("C004");
        when(courseRepo.findById("C004")).thenReturn(Optional.of(course));
        when(reviewRepo.calculateAverageRatingByCourse(course)).thenReturn(Optional.of(4.5));
        double result = reviewService.getAverageRating("C004");
        assertEquals(4.5, result);
    }
}
