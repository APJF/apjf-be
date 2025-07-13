package fu.sep.apjf.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO cho thông tin profile người dùng
 */
@Data
@Builder
public class UserProfileDto {
    private Long id;
    private String email;
    private String username;
    private String avatar;
    private boolean enabled;
    private List<String> authorities;
}
