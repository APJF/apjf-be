package fu.sep.apjf.dto.response;

import java.time.Instant;

public record UnitProgressResponseDto(
        String unitId,
        String unitTitle,
        boolean completed,
        Instant completedAt
) {
}