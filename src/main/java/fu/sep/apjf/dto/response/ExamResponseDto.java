package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;
import java.util.List;

public record ExamResponseDto(
        String id,
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType,
        LocalDateTime createdAt,
        List<QuestionResponseDto> questions,
        int totalQuestions,
        String courseId,
        String chapterId,
        String unitId,
        List<String> questionIds,
        int questionCount
) {
}
