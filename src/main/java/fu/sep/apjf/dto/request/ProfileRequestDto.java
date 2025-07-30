package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ProfileRequestDto(
    @Email(message = "Email không hợp lệ")
    String email,
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3-50 ký tự")
    String username,
    String phone,
    String avatar
) {}
