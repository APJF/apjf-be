package fu.sep.apjf.dto.response;

import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import fu.sep.apjf.entity.User;

import java.time.Instant;
import java.util.List;

public record PostResponseDto(
        String id,
        String content,
        Instant createdAt,
        String email,
        String avatar,
        List<CommentResponseDto> comments
) {}


