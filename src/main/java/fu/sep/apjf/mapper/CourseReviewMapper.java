package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.CourseReviewDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseReview;
import fu.sep.apjf.entity.User;

public final class CourseReviewMapper {

    private CourseReviewMapper() {}

    public static CourseReviewDto toDto(CourseReview review) {
        if (review == null) return null;

        return new CourseReviewDto(
                review.getId(),
                review.getCourse().getId(),
                review.getUser().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getLastUpdatedAt()
        );
    }

    public static CourseReview toEntity(CourseReviewDto dto, Course course, User user) {
        if (dto == null || course == null || user == null) return null;

        return CourseReview.builder()
                .id(dto.id())
                .course(course)
                .user(user)
                .rating(dto.rating())
                .comment(dto.comment())
                .createdAt(dto.createdAt() != null ? dto.createdAt() : java.time.LocalDateTime.now())
                .lastUpdatedAt(java.time.LocalDateTime.now())
                .build();
    }
}
