package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

public record CourseResponseDto(
        String id,
        String title,
        String description,
        Float duration,
        EnumClass.Level level,
        String image,
        String requirement,
        EnumClass.Status status,
        String prerequisiteCourseId,
        Float averageRating
) {
}
