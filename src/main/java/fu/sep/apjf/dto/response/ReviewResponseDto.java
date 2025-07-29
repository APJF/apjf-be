package fu.sep.apjf.dto.response;

public record ReviewResponseDto(
        Long userId,
        String courseId,
        int rating,
        String comment
) {
}
