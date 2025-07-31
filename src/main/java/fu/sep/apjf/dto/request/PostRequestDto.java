package fu.sep.apjf.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequestDto (
        String id,
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(min = 1, max = 255, message = "Tiêu đề phải từ 1 đến 255 ký tự")
        String title ,
        @NotBlank(message = "Content không được để trống")
        @Size(min = 1, max = 255, message = "Content phải từ 1 đến 255 ký tự")
        String content,
        @NotBlank(message = "User ID không được để trống")
        String userId) {
}

