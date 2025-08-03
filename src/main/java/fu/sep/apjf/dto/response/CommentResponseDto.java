package fu.sep.apjf.dto.response;

import java.time.Instant;

public record CommentResponseDto(
        String id,
        String content,
        Instant createdAt,
        String email,
        String avatar,
        String postId
) {}


