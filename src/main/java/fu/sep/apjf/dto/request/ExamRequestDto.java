package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record ExamRequestDto(
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
        List<String> questionIds
) {
        public static ExamRequestDto of(String id, String title, String description, Float duration,
                                        EnumClass.ExamType type, EnumClass.ExamScopeType scope,
                                        EnumClass.GradingMethod gradingMethod,
                                        String courseId, String chapterId, String unitId, List<String> questionIds) {
                return new ExamRequestDto(id, title, description, duration, type, scope, gradingMethod,
                        courseId, chapterId, unitId, questionIds);
        }
}
