package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;

public record ExamHistoryDto(
        String examResultId,
        String examId,
        String examTitle,
        String examDescription,
        EnumClass.ExamScopeType examScopeType,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        Float score,
        EnumClass.ExamStatus status,
        String courseId,
        String courseTitle,
        String chapterId,
        String chapterTitle,
        String unitId,
        String unitTitle,
        int totalQuestions,
        int correctAnswers,
        Double duration
) {
}
