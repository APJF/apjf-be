package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.EnumClass.ExamStatus;

import java.time.LocalDateTime;

public record ExamHistoryResponseDto(
        String examResultId,
        String examId,
        String examTitle,
        double score,
        ExamStatus status,
        EnumClass.ExamType type,
        LocalDateTime submittedAt
) {}
