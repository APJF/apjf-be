package fu.sep.apjf.dto.response;

import java.time.Instant;

public record CommentResponseDto(
        Long id,
        String content,
        Instant createdAt,
        String email,
        String avatar,
        Long postId
) {}


