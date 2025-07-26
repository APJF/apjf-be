package fu.sep.apjf.dto;

public record CourseWithRatingDto(
    String id,
    String title,
    String description,
    String imageUrl,
    Double averageRating
) {
}