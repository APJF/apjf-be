package fu.sep.apjf.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequestDto(
        @NotNull Long userId,
        @NotNull String courseId,
        @Min(1) @Max(5) int rating,
        String comment
) {}
