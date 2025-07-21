package fu.sep.apjf.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CourseReviewDto(
        Long id,

        @NotBlank(message = "ID khóa học không được để trống")
        String courseId,

        @NotNull(message = "ID người dùng không được để trống")
        Long userId,

        @NotNull(message = "Số sao không được để trống")
        Integer rating,

        @Size(max = 2000, message = "Bình luận không được vượt quá 2000 ký tự")
        String comment,

        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt
) {}
