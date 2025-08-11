package fu.sep.apjf.dto.response;

import java.time.Instant;

public record PostReportResponseDto(
        Long id,
        String content,
        Instant createdAt,
        Long userId,
        Long postId
) {}

