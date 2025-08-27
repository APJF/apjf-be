package fu.sep.apjf.dto.response;

import java.time.Instant;
import java.util.Set;

public record CourseProgressResponseDto(
        boolean completed,
        float percent
) {
}