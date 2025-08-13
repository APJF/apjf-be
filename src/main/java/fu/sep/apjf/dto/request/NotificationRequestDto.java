package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequestDto(

        @NotNull(message = "Nội dung không được để trống")
        @Size(min = 1, max = 255, message = "Nội dung phải từ 1 đến 255 ký tự")
        String content,

        @NotNull(message = "Người gửi không được để trống")
        Long senderId,

        @NotNull(message = "Bài viết không được để trống")
        Long postId
) {}
