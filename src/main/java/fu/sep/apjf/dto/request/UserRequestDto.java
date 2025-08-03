package fu.sep.apjf.dto.request;

public record UserRequestDto(
    String email,
    String username,
    String phone,
    String avatar
) {}
