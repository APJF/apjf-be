package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.CourseDto;
import fu.sep.apjf.dto.ExamSummaryDto;
import fu.sep.apjf.dto.TopicDto;
import fu.sep.apjf.entity.Course;

import java.util.Collections;
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

        Set<ExamSummaryDto> examDtos = course.getExams().stream()
                .map(ExamSummaryMapper::toDto)
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
                topicDtos,
                examDtos,
                Collections.emptySet() // We don't include chapters in the standard CourseDto
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