package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;


public record UpdateUserAuthoritiesDto(
        @NotNull(message = "User ID không được null")
        Long userId,
        @NotNull(message = "Authority IDs không được null")
        List<Long> authorityIds
) {
}