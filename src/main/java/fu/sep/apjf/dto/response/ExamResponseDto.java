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
        String courseId,
        String chapterId,
        String unitId,
        List<QuestionResponseDto> questions, // Có thể null nếu chỉ cần ID
        List<String> questionIds,
        int totalQuestions
) {}
