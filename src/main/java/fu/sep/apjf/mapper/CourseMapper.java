package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.CourseDto;
import fu.sep.apjf.dto.TopicDto;
import fu.sep.apjf.entity.Course;

import java.util.Set;
import java.util.stream.Collectors;

public final class CourseMapper {

    private CourseMapper() {
        // Private constructor to prevent instantiation
    }

    public static CourseDto toDto(Course course) {
        if (course == null) {
            return null;
        }

        Set<TopicDto> topicDtos = course.getTopics().stream()
                .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                .collect(Collectors.toSet());

        return new CourseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                course.getImage(),
                course.getRequirement(),
                course.getStatus(),
                course.getPrerequisiteCourse() != null ? course.getPrerequisiteCourse().getId() : null,
                topicDtos
        );
    }

    public static Course toEntity(CourseDto courseDto) {
        if (courseDto == null) {
            return null;
        }

        Course course = new Course();
        course.setId(courseDto.id());
        course.setTitle(courseDto.title());
        course.setDescription(courseDto.description());
        course.setDuration(courseDto.duration());
        course.setLevel(courseDto.level());
        course.setImage(courseDto.image());
        course.setRequirement(courseDto.requirement());
        course.setStatus(courseDto.status());

        return course;
    }
}
