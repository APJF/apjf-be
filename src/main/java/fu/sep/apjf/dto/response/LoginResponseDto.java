package fu.sep.apjf.dto.response;

public record LoginResponseDto(
    String access_token,
    String refresh_token,
    String token_type,
    long expires_in
) {}