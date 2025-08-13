package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;
import java.util.List;

public record ExamResultResponseDto(
        Long examResultId,
        String examId,
        String examTitle,
        Float score,
        Instant submittedAt,
        EnumClass.ExamStatus status,
        List<QuestionResultResponseDto> questionResults
) {
}