package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.EnumClass.ExamStatus;

import java.time.Instant;

public record ExamHistoryResponseDto(
        String examResultId,
        String examId,
        String examTitle,
        Float score,
        ExamStatus status,
        EnumClass.ExamType type,
        Instant submittedAt
) {
}