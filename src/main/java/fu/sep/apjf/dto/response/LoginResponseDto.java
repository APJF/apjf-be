package fu.sep.apjf.dto.response;

public record LoginResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType
) {}
