package fu.sep.apjf.dto.response;

import java.time.LocalDateTime;

/**
 * DTO để hiển thị tiến trình của học viên trong một bài học
 */
public record UnitProgressDto(
    String unitId,
    Long userId,
    boolean passed,  // Đổi từ "isPassed" thành "passed" để consistent với entity
    LocalDateTime passedAt
) {
}

