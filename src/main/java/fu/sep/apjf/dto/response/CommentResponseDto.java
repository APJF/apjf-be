package fu.sep.apjf.dto.response;

public record CommentResponseDto(
        String id,
        String content,
        String userId,
        String postId
) {}


