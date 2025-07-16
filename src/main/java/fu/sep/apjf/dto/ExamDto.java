package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;
import java.util.List;

public record ExamDto(
        String id,
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType,
        LocalDateTime createdAt,
        List<QuestionDto> questions,
        int totalQuestions,
        String courseId,
        String chapterId,
        String unitId,
        List<String> questionIds,
        int questionCount
) {
}
