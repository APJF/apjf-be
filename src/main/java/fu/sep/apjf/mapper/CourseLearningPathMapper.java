package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.CourseOrderDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseLearningPath;
import fu.sep.apjf.entity.CourseLearningPathKey;
import fu.sep.apjf.entity.LearningPath;

public class CourseLearningPathMapper {

    private CourseLearningPathMapper() {}

    public static CourseOrderDto toDto(CourseLearningPath entity) {
        return new CourseOrderDto(
                entity.getCourse().getId(),
                entity.getLearningPath().getId(),
                entity.getCourseOrderNumber()
        );
    }

    public static CourseLearningPath toEntity(CourseOrderDto dto, Course course, LearningPath path) {
        return CourseLearningPath.builder()
                .id(new CourseLearningPathKey(dto.courseId(), dto.learningPathId()))
                .course(course)
                .learningPath(path)
                .courseOrderNumber(dto.courseOrderNumber())
                .build();
    }
}

