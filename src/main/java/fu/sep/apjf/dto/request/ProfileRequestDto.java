package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProfileRequestDto(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        String email,

        @NotBlank(message = "Tên người dùng không được để trống")
        @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3-50 ký tự")
        String username,

        String avatar,

        List<String> authorities
) {
}
