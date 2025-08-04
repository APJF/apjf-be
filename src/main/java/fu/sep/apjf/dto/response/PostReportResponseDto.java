package fu.sep.apjf.dto.response;

import java.time.Instant;

public record PostReportResponseDto(
        String id,
        String content,
        Instant createdAt,
        String userId,
        String postId
) {}

