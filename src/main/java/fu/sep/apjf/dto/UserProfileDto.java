package fu.sep.apjf.dto;

import java.util.List;


public record UserProfileDto(
        String id,
        String email,
        String username,
        String avatar,
        boolean enabled,
        List<String> authorities
) {
}
