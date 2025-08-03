package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.dto.response.ProfileResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public final class UserMapper {

    private static MinioService minioService;

    @Autowired
    public void setMinioService(MinioService minioService) {
        UserMapper.minioService = minioService;
    }

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

        // Convert object name to presigned URL for avatar with exception handling
        String avatarUrl = null;
        try {
            avatarUrl = minioService.getAvatarUrl(user.getAvatar());
        } catch (Exception e) {
            // Log error and return null if failed to generate presigned URL
            System.err.println("Failed to generate avatar URL: " + e.getMessage());
        }

        return new ProfileResponseDto(
                user.getId().toString(),
                user.getUsername(),  // Đúng thứ tự: username trước
                user.getEmail(),     // email sau
                user.getPhone(),
                avatarUrl,  // Trả về presigned URL hoặc null nếu có lỗi
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

        // Convert object name to presigned URL for avatar with exception handling
        String avatarUrl = null;
        try {
            avatarUrl = minioService.getAvatarUrl(user.getAvatar());
        } catch (Exception e) {
            // Log error and return null if failed to generate presigned URL
            System.err.println("Failed to generate avatar URL in login: " + e.getMessage());
        }

        // Create the nested UserInfo object with presigned URL
        LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                avatarUrl,  // Trả về presigned URL thay vì object name
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
