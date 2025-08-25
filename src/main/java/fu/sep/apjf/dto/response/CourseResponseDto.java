package fu.sep.apjf.dto.response;

import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.entity.EnumClass;

import java.util.Set;

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
        Set<TopicDto> topics,
        Float averageRating,
        boolean isEnrolled,
        int totalStudent
) {
}
