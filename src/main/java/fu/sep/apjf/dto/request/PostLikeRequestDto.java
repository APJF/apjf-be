package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotNull;

public record PostLikeRequestDto(
        @NotNull(message = "PostId không được để trống")
        Long postId
) {}
