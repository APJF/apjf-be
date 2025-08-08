//package fu.sep.apjf.mapper;
//
//import fu.sep.apjf.dto.request.CourseProgressRequestDto;
//import fu.sep.apjf.dto.response.CourseProgressResponseDto;
//import fu.sep.apjf.entity.CourseProgress;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring", uses = {ChapterProgressMapper.class})
//public interface CourseProgressMapper {
//
//    @Mapping(target = "courseId", source = "id.courseId")
//    @Mapping(target = "userId", source = "id.userId")
//    @Mapping(target = "courseTitle", source = "course.title")
//    @Mapping(target = "chapterProgresses", source = "chapterProgresses")
//    CourseProgressResponseDto toDto(CourseProgress entity);
//
//    @Mapping(target = "id.courseId", source = "courseId")
//    @Mapping(target = "id.userId", source = "userId")
//    @Mapping(target = "course", ignore = true)
//    @Mapping(target = "user", ignore = true)
//    @Mapping(target = "chapterProgresses", ignore = true)
//    CourseProgress toEntity(CourseProgressRequestDto dto);
//}