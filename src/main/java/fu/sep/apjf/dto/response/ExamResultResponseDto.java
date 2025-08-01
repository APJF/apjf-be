package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;
import java.util.List;

public record ExamResultResponseDto(
        String id,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        Float score,
        EnumClass.ExamStatus status,
        String userId,
        String examId,
        String examTitle,
        List<ExamResultDetailDto> answers,
        int totalQuestions,
        int correctAnswers
) {}

