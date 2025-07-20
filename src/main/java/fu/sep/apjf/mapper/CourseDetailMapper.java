package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ChapterDto;
import fu.sep.apjf.dto.CourseDetailDto;
import fu.sep.apjf.dto.CourseDto;
import fu.sep.apjf.dto.ExamSummaryDto;
import fu.sep.apjf.entity.Course;

import java.util.Set;
import java.util.stream.Collectors;

public final class CourseDetailMapper {
    private CourseDetailMapper() {
    }

    public static CourseDetailDto toDto(Course course) {
        if (course == null) {
            return null;
        }

        // Create a detailed CourseDto with chapters and exams nested
        CourseDto courseDto = createDetailedCourseDto(course);

        return new CourseDetailDto(courseDto);
    }

    private static CourseDto createDetailedCourseDto(Course course) {
        // Get course exams
        Set<ExamSummaryDto> courseExams = course.getExams().stream()
                .map(ExamSummaryMapper::toDto)
                .collect(Collectors.toSet());

        // Get chapters with their units and exams
        Set<ChapterDto> chapterDtos = course.getChapters().stream()
                .map(ChapterMapper::toDto)
                .collect(Collectors.toSet());

        // Create the course DTO with all its relationships
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
                course.getTopics().stream()
                        .map(topic -> new fu.sep.apjf.dto.TopicDto(topic.getId(), topic.getName()))
                        .collect(Collectors.toSet()),
                courseExams,
                chapterDtos
        );
    }
}
