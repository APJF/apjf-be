package fu.sep.apjf.dto.response;

import java.time.Instant;

public record CommentReportResponseDto(
        String id,
        String content,
        Instant createdAt,
        String userId,
        String commentId
) {}



