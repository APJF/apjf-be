package fu.sep.apjf.dto.response;

import java.time.Instant;

public record NotificationResponseDto(
        Long id,
        String content,
        boolean read,
        Instant createdAt,
        Long senderId,
        String senderUsername,
        Long postId,
        String postTitle
) {
}