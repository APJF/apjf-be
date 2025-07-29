package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

/**
 * DTO đại diện cho yêu cầu tạo/cập nhật lộ trình học tập
 */
public record LearningPathRequestDto(
        String title,
        String description,
        EnumClass.Level targetLevel,
        String primaryGoal,
        String focusSkill,
        EnumClass.Status status,
        Integer duration,
        Long userId
) {
    public static LearningPathRequestDto of(String title, String description, EnumClass.Level targetLevel,
                                     String primaryGoal, String focusSkill, EnumClass.Status status,
                                     Integer duration, Long userId) {
        return new LearningPathRequestDto(title, description, targetLevel, primaryGoal, focusSkill,
                                        status, duration, userId);
    }
}
