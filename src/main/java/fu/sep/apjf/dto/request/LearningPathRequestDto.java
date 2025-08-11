package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

/**
 * DTO đại diện cho yêu cầu tạo/cập nhật lộ trình học tập
 */
public record LearningPathRequestDto(
        Long id,
        String title,
        String description,
        EnumClass.Level targetLevel,
        String primaryGoal,
        String focusSkill,
        Float duration,
        Long userId,
        List<String> courseIds
) {
    public static LearningPathRequestDto of(Long id, String title, String description, EnumClass.Level targetLevel,
                                            String primaryGoal, String focusSkill,
                                            Float duration, Long userId, List<String> courseIds) {
        return new LearningPathRequestDto(id, title, description, targetLevel, primaryGoal, focusSkill,
                 duration, userId, courseIds);
    }
}
