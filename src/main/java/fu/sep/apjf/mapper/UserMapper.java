package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.dto.response.ProfileResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.MinioService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected MinioService minioService;

    // Simplified mapping - loại bỏ complex @Named methods
    @Mapping(target = "id", source = "id", qualifiedByName = "convertId")
    @Mapping(target = "avatar", source = "avatar", qualifiedByName = "convertAvatarUrl")
    @Mapping(target = "authorities", ignore = true) // Tạm ignore để tránh lỗi
    public abstract ProfileResponseDto toProfileDto(User user);

    // Simplified mapping cho LoginResponseDto
    @Mapping(target = "accessToken", source = "token")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "userInfo.id", source = "user.id")
    @Mapping(target = "userInfo.username", source = "user.username")
    @Mapping(target = "userInfo.email", source = "user.email")
    @Mapping(target = "userInfo.avatar", source = "user.avatar", qualifiedByName = "convertAvatarUrl")
    @Mapping(target = "userInfo.roles", ignore = true) // Tạm ignore để tránh lỗi
    public abstract LoginResponseDto toLoginResponseDto(User user, String token);

    // Custom mapping methods
    @Named("convertId")
    protected String convertId(Long id) {
        return id != null ? id.toString() : null;
    }

    @Named("convertAvatarUrl")
    protected String convertAvatarUrl(String avatar) {
        if (avatar == null) {
            return null;
        }
        try {
            return minioService.getAvatarUrl(avatar);
        } catch (Exception e) {
            System.err.println("Failed to generate avatar URL: " + e.getMessage());
            return null;
        }
    }

    @Named("convertAuthorities")
    protected List<String> convertAuthorities(List<GrantedAuthority> authorities) {
        if (authorities == null) {
            return java.util.Collections.emptyList();
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
