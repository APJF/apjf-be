package fu.sep.apjf.dto.response;

public record ReviewResponseDto(
        Long userId,
        String courseId,
        Float rating,
        String comment
) {
}