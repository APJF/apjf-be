package fu.sep.apjf.dto.response;

import java.time.Instant;

public record CommentReportResponseDto(
        Long id,
        String content,
        Instant createdAt,
        Long userId,
        Long commentId
) {}



