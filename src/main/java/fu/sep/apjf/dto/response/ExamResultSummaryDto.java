package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;

public record ExamResultSummaryDto(
        String id,
        String examTitle,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        Float score,
        EnumClass.ExamStatus status
) {}

