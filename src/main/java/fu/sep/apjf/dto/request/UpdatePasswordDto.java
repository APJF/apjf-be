package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDto(
        @NotBlank(message = "Mật khẩu hiện tại không được để trống")
        @Size(min = 6, message = "Mật khẩu ít nhất 6 ký tự")
        String currentPassword,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 6, message = "Mật khẩu mới ít nhất 6 ký tự")
        String newPassword,

        @NotBlank(message = "Xác nhận mật khẩu mới không được để trống")
        String confirmNewPassword
) {
}