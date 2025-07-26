package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.dto.response.ProfileResponseDto;
import fu.sep.apjf.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public final class UserMapper {

    private UserMapper() {
        // Private constructor to prevent instantiation
    }

    public static ProfileResponseDto toProfileDto(User user) {
        if (user == null) {
            return null;
        }

        List<String> authorities = new ArrayList<>();
        if (user.getAuthorities() != null) {
            authorities = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }

        return new ProfileResponseDto(
                user.getId().toString(),
                user.getEmail(),
                user.getUsername(),
                user.getAvatar(),
                user.isEnabled(),
                authorities
        );
    }

    public static LoginResponseDto toLoginResponseDto(User user, String token) {
        if (user == null) {
            return null;
        }

        List<String> roles = new ArrayList<>();
        if (user.getAuthorities() != null) {
            roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }

        // Create the nested UserInfo object with the correct parameter order
        LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getAvatar(),
                roles
        );

        // Default token configuration values
        String tokenType = "Bearer";
        String refreshToken = ""; // Optional, leave empty if not used

        return new LoginResponseDto(
                token,
                refreshToken,
                tokenType,
                userInfo
        );
    }
}
