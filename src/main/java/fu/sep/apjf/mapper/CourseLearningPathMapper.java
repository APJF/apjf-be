package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.entity.CourseLearningPath;
import fu.sep.apjf.entity.CourseLearningPathKey;
import fu.sep.apjf.entity.LearningPath;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseLearningPathMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "learningPathId", source = "learningPath.id")
    @Mapping(target = "courseOrderNumber", source = "courseOrderNumber")
    @Mapping(target = "title", source = "course.title")
    @Mapping(target = "description", source = "course.description")
    @Mapping(target = "duration", source = "course.duration")
    @Mapping(target = "level", source = "course.level")
    CourseOrderDto toDto(CourseLearningPath entity);

    default CourseLearningPath toEntity(CourseOrderDto dto, Course course, LearningPath path) {
        if (dto == null || course == null || path == null) return null;

        return CourseLearningPath.builder()
                .id(new CourseLearningPathKey(course.getId(), path.getId()))
                .course(course)
                .learningPath(path)
                .courseOrderNumber(dto.courseOrderNumber())
                .build();
    }
}
