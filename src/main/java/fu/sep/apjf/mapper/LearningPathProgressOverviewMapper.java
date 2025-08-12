package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.LearningPathProgressOverviewDto;
import fu.sep.apjf.entity.LearningPathProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LearningPathProgressOverviewMapper {

    @Mapping(target = "learningPathId", expression = "java(String.valueOf(entity.getLearningPath().getId()))")
    @Mapping(target = "learningPathTitle", source = "entity.learningPath.title")
    @Mapping(target = "totalUnits", source = "totalUnits")
    @Mapping(target = "completedUnits", source = "completedUnits")
    @Mapping(target = "progressPercentage", source = "progressPercentage")
    LearningPathProgressOverviewDto toDto(
            LearningPathProgress entity,
            int totalUnits,
            int completedUnits,
            double progressPercentage
    );
}
