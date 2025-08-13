package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;

/**
 * DTO tối ưu cho danh sách exams - không bao gồm questions để cải thiện performance
 */
public record ExamListResponseDto(
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
        Integer totalQuestions  // Chỉ trả về số lượng thay vì full questions
) {
}
