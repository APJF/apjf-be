package fu.sep.apjf.dto.response;

public record PostLikeResponseDto(
        boolean liked,      // true = vừa like, false = vừa unlike
        int totalLikes      // tổng số like của post
) {}
