package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.Course;
import org.mapstruct.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ExamSummaryMapper.class})
public interface CourseMapper {

    // Mặc định không load exams (cho findAll)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "topics", source = "topics", qualifiedByName = "mapTopics")
    @Mapping(target = "prerequisiteCourseId", source = "prerequisiteCourse.id")
    @Mapping(target = "averageRating", ignore = true)
    // id, title, description, duration, level, image, requirement, status tự động map
    CourseResponseDto toDto(Course course);

    // Load cả exams (cho findById)
    @Mapping(target = "prerequisiteCourseId", source = "prerequisiteCourse.id")
    @Mapping(target = "topics", source = "topics", qualifiedByName = "mapTopics")
    @Mapping(target = "averageRating", ignore = true)
    // exams, id, title, description, etc. tự động map
    CourseResponseDto toDtoWithExams(Course course);

    // Map với averageRating
    @Mapping(target = "prerequisiteCourseId", source = "course.prerequisiteCourse.id")
    @Mapping(target = "topics", source = "course.topics", qualifiedByName = "mapTopics")
    // exams, averageRating, và các fields khác tự động map
    CourseResponseDto toDto(Course course, Double averageRating);

    // Map với averageRating (double primitive)
    @Mapping(target = "prerequisiteCourseId", source = "course.prerequisiteCourse.id")
    @Mapping(target = "topics", source = "course.topics", qualifiedByName = "mapTopics")
    CourseResponseDto toDto(Course course, double averageRating);

    // Entity mapping (giữ lại cho create/update)
    @Mapping(target = "status", constant = "INACTIVE")
    @Mapping(target = "prerequisiteCourse", ignore = true)
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "approvalRequests", ignore = true)
    @Mapping(target = "courseLearningPaths", ignore = true)
    // id, title, description, duration, level, image, requirement tự động map
    Course toEntity(CourseRequestDto courseDto);

    // Custom mapping methods
    @Named("mapTopics")
    default Set<TopicDto> mapTopics(Set<fu.sep.apjf.entity.Topic> topics) {
        if (topics == null || topics.isEmpty()) {
            return Collections.emptySet();
        }
        return topics.stream()
                .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                .collect(Collectors.toSet());
    }
}