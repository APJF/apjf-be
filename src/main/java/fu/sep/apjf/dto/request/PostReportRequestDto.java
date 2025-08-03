package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostReportRequestDto(
        String id,

        @NotBlank(message = "Nội dung báo cáo không được để trống")
        @Size(min = 1, max = 255, message = "Nội dung phải từ 1 đến 255 ký tự")
        String content,

        @NotBlank(message = "User ID không được để trống")
        String userId,

        @NotBlank(message = "Post ID không được để trống")
        String postId
) {}


