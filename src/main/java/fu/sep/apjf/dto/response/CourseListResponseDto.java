package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.Set;

/**
 * DTO tối ưu cho danh sách courses - không bao gồm exams để cải thiện performance
 */
public record CourseListResponseDto(
        String id,
        String title,
        String description,
        Float duration,
        EnumClass.Level level,
        String image,           // Giữ nguyên field image như entity, null nếu không có
        String requirement,
        EnumClass.Status status,
        String prerequisiteCourseId,
        Set<String> topics,
        Float averageRating
) {
}
