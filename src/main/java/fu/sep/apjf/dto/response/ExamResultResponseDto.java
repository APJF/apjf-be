package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;
import java.util.List;

public record ExamResultResponseDto(
        Long id,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        Float score,
        EnumClass.ExamStatus status,
        Long userId,
        String examId,  // Exam ID vẫn là String
        String examTitle,
        List<ExamResultDetailDto> answers,
        int totalQuestions,
        int correctAnswers
) {}
