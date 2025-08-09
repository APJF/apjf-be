package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(

        @NotBlank(message = "Nội dung bình luận không được để trống")
        @Size(min = 1, max = 255, message = "Nội dung phải từ 1 đến 255 ký tự")
        String content,

        @NotNull(message = "User ID không được để trống")
        Long userId,

        @NotNull(message = "Post ID không được để trống")
        Long postId
) {}
