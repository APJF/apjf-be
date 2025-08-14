// ExamResponseDto.java
package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;
import java.util.List;

/**
 * DTO cho exam detail - không bao gồm questions (sẽ có endpoint riêng)
 */
public record ExamDetailResponseDto(
        String id,
        String title,
        String description,
        Float duration,
        EnumClass.ExamType type,
        EnumClass.ExamScopeType examScopeType,
        EnumClass.GradingMethod gradingMethod,
        String courseId,
        String chapterId,
        String unitId,
        Instant createdAt,
        List<QuestionResponseDto> questions

) {
}
