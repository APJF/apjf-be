package fu.sep.apjf.dto.response;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        Long id,
        String content,
        boolean isRead,
        LocalDateTime createdAt,

        Long senderId,
        String senderUsername,

        Long postId,
        String postTitle
) {}

