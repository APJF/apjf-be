package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @Email(message = "Email không đúng định dạng")
        @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
        String email,

        @Size(max = 255, message = "Username không được vượt quá 255 ký tự")
        String username,

        @Size(max = 10, message = "Số điện thoại không được vượt quá 10 ký tự")
        String phone,

        @Size(max = 255, message = "Avatar URL không được vượt quá 255 ký tự")
        String avatar
) {
}
