package fu.sep.apjf.dto.response;

import java.time.LocalDateTime;

public record UnitProgressResponseDto(
        String unitId,
        String unitTitle,
        boolean completed,
        LocalDateTime completedAt
) {
}