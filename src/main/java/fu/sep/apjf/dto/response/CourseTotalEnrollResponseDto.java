package fu.sep.apjf.dto.response;

import java.time.YearMonth;

public record CourseTotalEnrollResponseDto(
        YearMonth month,
        int totalEnrolled,
        int totalCompleted
) {
}

