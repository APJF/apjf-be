package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.CourseWithRatingDto;
import fu.sep.apjf.entity.Course;

import java.util.Collections;

public final class CourseWithRatingMapper {

    private CourseWithRatingMapper() {}

    public static CourseWithRatingDto toDto(Course course, Double averageRating) {
        if (course == null) return null;

        return new CourseWithRatingDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                course.getImage(),
                course.getRequirement(),
                course.getStatus(),
                course.getPrerequisiteCourse(),
                Collections.emptyList(),
                averageRating
        );
    }
}

