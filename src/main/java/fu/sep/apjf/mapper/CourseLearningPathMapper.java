package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseLearningPath;
import fu.sep.apjf.entity.CourseLearningPathKey;
import fu.sep.apjf.entity.LearningPath;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseLearningPathMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "learningPathId", source = "learningPath.id")
    @Mapping(target = "courseOrderNumber", source = "courseOrderNumber")
    CourseOrderDto toDto(CourseLearningPath entity);

    // Entity mapping với composite key
    default CourseLearningPath toEntity(CourseOrderDto dto, Course course, LearningPath path) {
        if (dto == null || course == null || path == null) {
            return null;
        }

        return CourseLearningPath.builder()
                .id(new CourseLearningPathKey(dto.courseId(), dto.learningPathId()))
                .course(course)
                .learningPath(path)
                .courseOrderNumber(dto.courseOrderNumber())
                .build();
    }
}
