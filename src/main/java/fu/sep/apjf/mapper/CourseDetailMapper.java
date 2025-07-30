package fu.sep.apjf.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.entity.Course;
import fu.sep.apjf.entity.Review;

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
        Set<ExamSummaryDto> examDtos;
        if (course.getExams() == null) {
            examDtos = new HashSet<>();
        } else {
            examDtos = course.getExams().stream()
                    .map(exam -> {
                        Integer duration = null;
                        if (exam.getDuration() != null) {
                            duration = exam.getDuration().intValue();
                        }

                        int questionCount = 0;
                        if (exam.getQuestions() != null) {
                            questionCount = exam.getQuestions().size();
                        }

                        return new ExamSummaryDto(
                                exam.getId(),
                                exam.getTitle(),
                                exam.getDescription(),
                                duration,
                                questionCount,
                                null);
                    })
                    .collect(Collectors.toSet());
        }

        // Calculate average rating if available
        double averageRating;
        if (course.getReviews() == null || course.getReviews().isEmpty()) {
            averageRating = 0.0;
        } else {
            averageRating = course.getReviews().stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
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
