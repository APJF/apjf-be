package fu.sep.apjf.dto;

import java.time.LocalDateTime;

public record CourseReviewDto(
    Long id,
    String courseId,
    Long userId,
    Integer rating,
    String comment,
    LocalDateTime createdAt,
    LocalDateTime lastUpdatedAt
) {
}