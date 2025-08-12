package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CourseProgressRequestDto;
import fu.sep.apjf.dto.response.CourseProgressResponseDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.CourseProgress;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ChapterProgressMapper.class})
public interface CourseProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", source = "course")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "completed", source = "dto.completed")
    @Mapping(target = "completedAt", expression = "java(dto.completed() ? java.time.LocalDateTime.now() : null)")
    CourseProgress toEntity(CourseProgressRequestDto dto, Course course, User user);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    CourseProgressResponseDto toResponseDto(CourseProgress entity);
}
