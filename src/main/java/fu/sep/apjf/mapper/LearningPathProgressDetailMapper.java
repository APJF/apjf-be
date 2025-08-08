//package fu.sep.apjf.mapper;
//
//import fu.sep.apjf.dto.response.LearningPathProgressDetailResponseDto;
//import fu.sep.apjf.entity.LearningPathProgress;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring", uses = {CourseProgressMapper.class})
//public interface LearningPathProgressDetailMapper {
//
//    @Mapping(target = "learningPathId", source = "learningPath.id")
//    @Mapping(target = "learningPathTitle", source = "learningPath.title")
//    @Mapping(target = "courseProgresses", source = "courseProgresses")
//    LearningPathProgressDetailResponseDto toDto(LearningPathProgress entity);
//}
