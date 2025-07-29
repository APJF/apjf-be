package fu.sep.apjf.dto.response;

import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.entity.EnumClass;

import java.math.BigDecimal;
import java.util.Set;

public record CourseResponseDto(
        String id,
        String title,
        String description,
        BigDecimal duration,
        EnumClass.Level level,
        String image,
        String requirement,
        EnumClass.Status status,
        String prerequisiteCourseId,
        Set<TopicDto> topics,
        Set<ExamSummaryDto> exams,
        Double averageRating
) {
}
