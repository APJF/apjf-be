package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.*;

public record ReviewRequestDto(
        @NotBlank(message = "ID khóa học không được để trống")
        String courseId,

        @DecimalMin(value = "1.0", message = "Số sao tối thiểu là 1")
        @DecimalMax(value = "5.0", message = "Số sao tối đa là 5")
        @NotNull(message = "Số sao không được để trống")
        Float rating,

        @Size(max = 2000, message = "Bình luận không được vượt quá 2000 ký tự")
        String comment
) {
}
