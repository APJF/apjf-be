package fu.sep.apjf.dto;

import java.util.List;

public record LoginResponseDto(
        String access_token,
        String token_type,
        int expires_in,
        String refresh_token,
        UserInfo user
) {
    public record UserInfo(
            Long id,
            String username,
            String avatar,
            List<String> roles
    ) {
    }
}
