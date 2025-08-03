package fu.sep.apjf.dto.response;

import java.util.List;

public record UserResponseDto(
    Long id,
    String email,
    String username,
    String avatar,
    List<String> authorities
) {}
