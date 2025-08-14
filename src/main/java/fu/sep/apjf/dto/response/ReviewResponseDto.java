package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.User;

import java.time.Instant;

public record ReviewResponseDto(
        Long id,
        String courseId,
        Float rating,
        String comment,
        Instant createdAt,
        UserSummaryDto user
) {
    public record UserSummaryDto(
            Long id,
            String username,
            String avatar
    ) {}
}