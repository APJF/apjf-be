package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ReviewRequestDto;
import fu.sep.apjf.dto.response.ReviewResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.ReviewMapper;
import fu.sep.apjf.repository.CourseProgressRepository;
import fu.sep.apjf.repository.CourseRepository;
import fu.sep.apjf.repository.ReviewRepository;
import fu.sep.apjf.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private MinioService minioService;

    @Mock
    private CourseProgressRepository courseProgressRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Course course;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        course = new Course();
        course.setId("C1");

        review = new Review();
        review.setId(100L);
        review.setUser(user);
        review.setCourse(course);
        review.setRating(5f);
        review.setComment("Great");
    }

    @Test
    void testAddReview_Success() {
        ReviewRequestDto dto = new ReviewRequestDto("C1", 5f, "Great");

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(courseRepo.findById(course.getId())).thenReturn(Optional.of(course));
        when(reviewRepo.findByUserAndCourse(user, course)).thenReturn(Optional.empty());
        when(reviewMapper.toEntity(dto, course, user)).thenReturn(review);
        when(reviewRepo.save(review)).thenReturn(review);

        ReviewResponseDto.UserSummaryDto userDto =
                new ReviewResponseDto.UserSummaryDto(user.getId(), user.getUsername(), null);
        ReviewResponseDto reviewDto =
                new ReviewResponseDto(review.getId(), course.getId(), review.getRating(), review.getComment(), null, userDto);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewResponseDto response = reviewService.addReview(user.getId(), dto);
        assertNotNull(response);
        assertEquals(100L, response.id());
    }

    @Test
    void testUpdateReview_Success() {
        ReviewRequestDto dto = new ReviewRequestDto("C1", 4f, "Updated");

        when(reviewRepo.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepo.save(review)).thenReturn(review);

        ReviewResponseDto.UserSummaryDto userDto =
                new ReviewResponseDto.UserSummaryDto(user.getId(), user.getUsername(), null);
        ReviewResponseDto reviewDto =
                new ReviewResponseDto(review.getId(), course.getId(), dto.rating(), dto.comment(), null, userDto);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewResponseDto response = reviewService.updateReview(review.getId(), user.getId(), dto);
        assertEquals(review.getId(), response.id());
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepo.findById(review.getId())).thenReturn(Optional.of(review));
        doNothing().when(reviewRepo).delete(review);

        assertDoesNotThrow(() -> reviewService.deleteReview(review.getId(), user.getId()));
        verify(reviewRepo).delete(review);
    }

    @Test
    void testGetReviewsByCourse_Success() {
        when(courseRepo.existsById(course.getId())).thenReturn(true);
        when(reviewRepo.findByCourseId(course.getId())).thenReturn(List.of(review));

        ReviewResponseDto.UserSummaryDto userDto =
                new ReviewResponseDto.UserSummaryDto(user.getId(), user.getUsername(), null);
        ReviewResponseDto reviewDto =
                new ReviewResponseDto(review.getId(), course.getId(), review.getRating(), review.getComment(), null, userDto);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        var reviews = reviewService.getReviewsByCourse(course.getId());
        assertEquals(1, reviews.size());
    }

    @Test
    void testGetReviewsByUser_Success() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepo.findByUser(user)).thenReturn(List.of(review));

        ReviewResponseDto.UserSummaryDto userDto =
                new ReviewResponseDto.UserSummaryDto(user.getId(), user.getUsername(), null);
        ReviewResponseDto reviewDto =
                new ReviewResponseDto(review.getId(), course.getId(), review.getRating(), review.getComment(), null, userDto);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        var reviews = reviewService.getReviewsByUser(user.getId());
        assertEquals(1, reviews.size());
    }

    @Test
    void testGetAverageRating_Success() {
        when(reviewRepo.calculateAverageRatingByCourseId(course.getId())).thenReturn(Optional.of(4.5f));
        Float avg = reviewService.getAverageRating(course.getId());
        assertEquals(4.5f, avg);
    }

}
