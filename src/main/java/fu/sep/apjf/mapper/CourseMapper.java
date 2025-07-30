package fu.sep.apjf.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.EnumClass;

/**
 * Mapper class để chuyển đổi giữa Course entity và CourseRequestDto
 */
public final class CourseMapper {
    private CourseMapper() {}

    public static CourseRequestDto toDto(Course course) {
        if (course == null) {
            return null;
        }

        // Get prerequisiteCourse ID if it exists
        String prerequisiteCourseId = course.getPrerequisiteCourse() != null ?
                course.getPrerequisiteCourse().getId() : null;

        // Extract topic IDs
        Set<String> topicIds = Collections.emptySet();
        if (course.getTopics() != null && !course.getTopics().isEmpty()) {
            topicIds = course.getTopics().stream()
                    .map(topic -> topic.getId().toString()) // Convert Integer to String
                    .collect(Collectors.toSet());
        }

        // Extract exam IDs
        Set<String> examIds = Collections.emptySet();
        if (course.getExams() != null && !course.getExams().isEmpty()) {
            examIds = course.getExams().stream()
                    .map(exam -> exam.getId()) 
                    .collect(Collectors.toSet());
        }

        return new CourseRequestDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                course.getImage(),
                course.getRequirement(),
                course.getStatus(),
                prerequisiteCourseId,
                topicIds,
                examIds
        );
    }

    public static Course toEntity(CourseRequestDto courseDto) {
        if (courseDto == null) {
            return null;
        }

        return Course.builder()
                .id(courseDto.id())
                .title(courseDto.title())
                .description(courseDto.description())
                .duration(courseDto.duration())
                .level(courseDto.level())
                .image(courseDto.image())
                .requirement(courseDto.requirement())
                .status(courseDto.status() != null ? courseDto.status() : EnumClass.Status.DRAFT)
                .build();
    }

    public static List<CourseRequestDto> toDtoList(List<Course> courses) {
        if (courses == null) {
            return Collections.emptyList();
        }
        return courses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    public static CourseResponseDto toResponseDto(Course course) {
        if (course == null) {
            return null;
        }

        // Get prerequisiteCourse ID if it exists
        String prerequisiteCourseId = course.getPrerequisiteCourse() != null ?
                course.getPrerequisiteCourse().getId() : null;

        // Chuyển đổi Topics thành TopicDto
        Set<TopicDto> topicDtos = Collections.emptySet();
        if (course.getTopics() != null && !course.getTopics().isEmpty()) {
            topicDtos = course.getTopics().stream()
                    .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                    .collect(Collectors.toSet());
        }

        // Chuyển đổi Exams thành ExamSummaryDto
        Set<ExamSummaryDto> examDtos = Collections.emptySet();
        if (course.getExams() != null && !course.getExams().isEmpty()) {
            examDtos = course.getExams().stream()
                    .map(exam -> new ExamSummaryDto(
                            exam.getId(),
                            exam.getTitle(),
                            exam.getDescription(),
                            exam.getDuration() != null ? exam.getDuration().intValue() : null,
                            exam.getQuestions() != null ? exam.getQuestions().size() : 0,
                            null)) // Trạng thái có thể null hoặc cần được xác định
                    .collect(Collectors.toSet());
        }

        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                course.getImage(),
                course.getRequirement(),
                course.getStatus(),
                prerequisiteCourseId,
                topicDtos,
                examDtos,
                null  // No average rating by default
        );
    }

    public static CourseResponseDto toResponseDtoWithRating(Course course, Double rating) {
        if (course == null) {
            return null;
        }

        // Get prerequisiteCourse ID if it exists
        String prerequisiteCourseId = course.getPrerequisiteCourse() != null ?
                course.getPrerequisiteCourse().getId() : null;

        // Chuyển đổi Topics thành TopicDto
        Set<TopicDto> topicDtos = Collections.emptySet();
        if (course.getTopics() != null && !course.getTopics().isEmpty()) {
            topicDtos = course.getTopics().stream()
                    .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                    .collect(Collectors.toSet());
        }

        // Chuyển đổi Exams thành ExamSummaryDto
        Set<ExamSummaryDto> examDtos = Collections.emptySet();
        if (course.getExams() != null && !course.getExams().isEmpty()) {
            examDtos = course.getExams().stream()
                    .map(exam -> new ExamSummaryDto(
                            exam.getId(),
                            exam.getTitle(),
                            exam.getDescription(),
                            exam.getDuration() != null ? exam.getDuration().intValue() : null,
                            exam.getQuestions() != null ? exam.getQuestions().size() : 0,
                            null))
                    .collect(Collectors.toSet());
        }

        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                course.getImage(),
                course.getRequirement(),
                course.getStatus(),
                prerequisiteCourseId,
                topicDtos,
                examDtos,
                rating
        );
    }

    public static CourseResponseDto toResponseDto(Course course, Double averageRating) {
        if (course == null) {
            return null;
        }
        // Get prerequisiteCourse ID if it exists
        String prerequisiteCourseId = course.getPrerequisiteCourse() != null ?
                course.getPrerequisiteCourse().getId() : null;

        Set<TopicDto> topicDtos = Collections.emptySet();
        if (course.getTopics() != null && !course.getTopics().isEmpty()) {
            topicDtos = course.getTopics().stream()
                    .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                    .collect(Collectors.toSet());
        }

        Set<ExamSummaryDto> examDtos = Collections.emptySet();
        if (course.getExams() != null && !course.getExams().isEmpty()) {
            examDtos = course.getExams().stream()
                    .map(exam -> new ExamSummaryDto(
                            exam.getId(),
                            exam.getTitle(),
                            exam.getDescription(),
                            exam.getDuration() != null ? exam.getDuration().intValue() : null,
                            exam.getQuestions() != null ? exam.getQuestions().size() : 0,
                            null))
                    .collect(Collectors.toSet());
        }

        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getLevel(),
                course.getImage(),
                course.getRequirement(),
                course.getStatus(),
                prerequisiteCourseId,
                topicDtos,
                examDtos,
                averageRating
        );
    }
}