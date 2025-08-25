package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.request.TopicDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.Course;
import org.mapstruct.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ExamOverviewMapper.class})
public interface CourseMapper {

    // Method cho list courses - trả về CourseResponseDto (không có topics và exams)
    @Mapping(target = "prerequisiteCourseId", source = "course.prerequisiteCourse.id")
    CourseResponseDto toDto(Course course, Float averageRating);

    // Method cho course detail - trả về CourseDetailResponseDto (có topics, không có exams)
    @Mapping(target = "prerequisiteCourseId", source = "course.prerequisiteCourse.id")
    @Mapping(target = "topics", source = "course.topics", qualifiedByName = "mapTopics")
    CourseResponseDto toDetailDto(Course course, Float averageRating);

    // Method cho course detail với presigned URL
    @Mapping(target = "prerequisiteCourseId", source = "course.prerequisiteCourse.id")
    @Mapping(target = "topics", source = "course.topics", qualifiedByName = "mapTopics")
    @Mapping(target = "image", source = "presignedImageUrl")
    @Mapping(target = "isEnrolled", source = "isEnrolled")
    @Mapping(target = "totalEnrolled", source = "totalEnrolled")
    CourseResponseDto toDetailDtoWithPresignedUrl(Course course, Float averageRating, String presignedImageUrl, boolean isEnrolled, int totalEnrolled);

    // Entity mapping (giữ lại cho create/update)
    @Mapping(target = "status", constant = "INACTIVE")
    @Mapping(target = "prerequisiteCourse", ignore = true)
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "approvalRequests", ignore = true)
    @Mapping(target = "courseLearningPaths", ignore = true)
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
