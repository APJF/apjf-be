// ExamResponseDto.java
package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;
import java.util.List;

public record ExamResponseDto(
        String id,
        String title,
        String description,
        Double duration,
        EnumClass.ExamType type,
        EnumClass.ExamScopeType examScopeType,
        EnumClass.GradingMethod gradingMethod,
        String courseId,
        String chapterId,
        String unitId,
        LocalDateTime createdAt,
        List<QuestionResponseDto> questions
) {
    public static ExamResponseDto of(String id, String title, String description, Double duration,
                                     EnumClass.ExamType type, EnumClass.ExamScopeType scope,
                                     EnumClass.GradingMethod gradingMethod,
                                     String courseId, String chapterId, String unitId,
                                     LocalDateTime createdAt, List<QuestionResponseDto> questions) {
        return new ExamResponseDto(id, title, description, duration, type, scope, gradingMethod,
                courseId, chapterId, unitId, createdAt, questions);
    }
}
