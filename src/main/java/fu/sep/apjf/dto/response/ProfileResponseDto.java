package fu.sep.apjf.dto.response;

import java.util.List;


public record ProfileResponseDto(
        String id,
        String username,
        String email,
        String phone,
        String avatar,
        boolean enabled,
        List<String> authorities
) {
}
