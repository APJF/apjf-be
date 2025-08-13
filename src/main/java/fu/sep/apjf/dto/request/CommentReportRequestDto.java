package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentReportRequestDto(

        @NotBlank(message = "Nội dung báo cáo không được để trống")
        @Size(min = 1, max = 255, message = "Nội dung phải từ 1 đến 255 ký tự")
        String content,

        @NotNull(message = "User ID không được để trống")
        Long userId,

        @NotNull(message = "Comment ID không được để trống")
        Long commentId
) {}
