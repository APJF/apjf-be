package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusDto(
        @NotNull(message = "User ID không được null")
        Long userId,

        @NotNull(message = "Enabled status không được null")
        Boolean enabled,

        String reason
) {}