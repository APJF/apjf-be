package fu.sep.apjf.dto.response;

import java.time.Instant;

public record PostResponseDto(
        Long id,
        String content,
        Instant createdAt,
        Instant updatedAt,
        String email,
        String avatar,
        Long commentsCount
) {
}
