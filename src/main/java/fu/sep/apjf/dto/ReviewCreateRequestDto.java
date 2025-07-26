package fu.sep.apjf.dto;

import jakarta.validation.constraints.*;

public record ReviewCreateRequestDto(
    @NotNull(message = "ID người dùng không được để trống")
    Long userId,
    
    @NotBlank(message = "ID khóa học không được để trống")
    String courseId,
    
    @Min(1) @Max(5)
    @NotNull(message = "Số sao không được để trống")
    Integer rating,
    
    @Size(max = 2000, message = "Bình luận không được vượt quá 2000 ký tự")
    String comment
) {
}