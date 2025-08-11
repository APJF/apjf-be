package fu.sep.apjf.dto.response;

import java.time.Instant;
import java.util.List;

public record PostResponseDto(
        Long id,
        String content,
        Instant createdAt,
        String email,
        String avatar,
        List<CommentResponseDto> comments
) {}
