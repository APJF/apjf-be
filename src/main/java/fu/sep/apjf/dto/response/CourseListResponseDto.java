package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;


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
        Float averageRating
) {
}
