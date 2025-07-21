package fu.sep.apjf.dto;

import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;

import java.math.BigDecimal;
import java.util.List;

public record CourseWithRatingDto(
        String id,
        String title,
        String description,
        BigDecimal duration,
        EnumClass.Level level,
        String image,
        String requirement,
        EnumClass.Status status,
        Course prerequisiteCourseId,
        List<String> topics,
        Double averageRating
) {}
