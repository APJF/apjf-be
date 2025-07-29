package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Topic;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class CourseDetailMapper {
    private CourseDetailMapper() {
    }

    public static CourseResponseDto toDto(Course course) {
        if (course == null) {
            return null;
        }

        // Get prerequisiteCourse ID if it exists
        String prerequisiteCourseId = course.getPrerequisiteCourse() != null ?
                course.getPrerequisiteCourse().getId() : null;

        // Convert topics to TopicDto objects
        Set<TopicDto> topicDtos = course.getTopics().stream()
                .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                .collect(Collectors.toSet());

        // Convert exams to ExamSummaryDto objects
        Set<ExamSummaryDto> examDtos = course.getExams() != null ?
                course.getExams().stream()
                        .map(exam -> new ExamSummaryDto(
                                exam.getId(),
                                exam.getTitle(),
                                exam.getDescription(),
                                exam.getDuration() != null ? exam.getDuration().intValue() : null,
                                exam.getQuestions() != null ? exam.getQuestions().size() : 0,
                                null))
                        .collect(Collectors.toSet()) :
                new HashSet<>();

        // Calculate average rating if available
        Double averageRating = course.getReviews().isEmpty() ? 0.0 :
                course.getReviews().stream()
                        .mapToDouble(review -> review.getRating())
                        .average()
                        .orElse(0.0);

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
