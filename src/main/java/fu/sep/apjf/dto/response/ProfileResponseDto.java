package fu.sep.apjf.dto.response;

import java.util.List;


public record ProfileResponseDto(
        String id,
        String email,
        String username,
        String avatar,
        boolean enabled,
        List<String> authorities
) {
}
