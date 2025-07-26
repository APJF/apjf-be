package fu.sep.apjf.dto.response;

import java.util.List;

/**
 * DTO cho thông tin đăng nhập thành công
 */
public record LoginResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType,
    UserInfo userInfo
) {
    public record UserInfo(
        Long id,
        String email,
        String username,
        String avatar,
        List<String> roles

    ) {}
}
