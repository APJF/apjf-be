package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.CourseWithRatingDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.Course;

public final class CourseWithRatingMapper {

    private CourseWithRatingMapper() {}

    public static CourseWithRatingDto toDto(Course course, Double averageRating) {
        if (course == null) return null;

        return new CourseWithRatingDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getImage(),
                averageRating
        );
    }
    
    public static CourseWithRatingDto fromCourseResponseDto(CourseResponseDto courseResponseDto) {
        if (courseResponseDto == null) return null;
        
        return new CourseWithRatingDto(
                courseResponseDto.id(),
                courseResponseDto.title(),
                courseResponseDto.description(),
                courseResponseDto.image(),
                courseResponseDto.averageRating() != null ?
                    courseResponseDto.averageRating() : 0.0
        );
    }
}