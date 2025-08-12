package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.LearningPathProgressRequestDto;
import fu.sep.apjf.dto.response.LearningPathProgressDetailResponseDto;
import fu.sep.apjf.entity.LearningPath;
import fu.sep.apjf.entity.LearningPathProgress;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CourseProgressMapper.class})
public interface LearningPathProgressDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "learningPath", source = "learningPath")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "completed", source = "dto.completed")
    @Mapping(target = "completedAt", expression = "java(dto.completed() ? java.time.LocalDateTime.now() : null)")
    LearningPathProgress toEntity(LearningPathProgressRequestDto dto, LearningPath learningPath, User user);

    @Mapping(target = "learningPathId", source = "learningPath.id")
    @Mapping(target = "learningPathTitle", source = "learningPath.title")
    LearningPathProgressDetailResponseDto toDetailResponseDto(LearningPathProgress entity);
}
