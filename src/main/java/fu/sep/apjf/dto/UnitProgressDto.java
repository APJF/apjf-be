package fu.sep.apjf.dto;

import java.time.LocalDateTime;

public record UnitProgressDto(
        String unitId,
        Long userId,
        boolean isPassed,
        LocalDateTime passedAt
) {}
