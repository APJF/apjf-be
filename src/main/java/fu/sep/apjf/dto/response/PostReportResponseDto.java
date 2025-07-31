package fu.sep.apjf.dto.response;

public record PostReportResponseDto(
        String id,
        String content,
        String userId,
        String postId
) {}

