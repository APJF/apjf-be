package fu.sep.apjf.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequestDto(
        Long id,
        @NotBlank(message = "Content không được để trống")
        @Size(min = 1, max = 255, message = "Content phải từ 1 đến 255 ký tự")
        String content) {
}

