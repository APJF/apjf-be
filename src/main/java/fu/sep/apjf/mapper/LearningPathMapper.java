package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.LearningPath;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface LearningPathMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "targetLevel", source = "targetLevel", qualifiedByName = "stringToLevel")
    @Mapping(target = "duration", source = "duration", qualifiedByName = "bigDecimalToInteger")
    @Mapping(target = "pathStatus", ignore = true) // Thêm ignore cho pathStatus
    LearningPathResponseDto toDto(LearningPath learningPath);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "courseLearningPaths", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    LearningPath toEntity(LearningPathRequestDto dto);

    // Entity mapping với User
    default LearningPath toEntity(LearningPathRequestDto dto, User user) {
        LearningPath learningPath = toEntity(dto);
        if (learningPath != null && user != null) {
            learningPath.setUser(user);
        }
        return learningPath;
    }

    // Custom mapping methods
    @Named("stringToLevel")
    default EnumClass.Level stringToLevel(String targetLevel) {
        if (targetLevel == null) return null;
        try {
            return EnumClass.Level.valueOf(targetLevel);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("bigDecimalToInteger")
    default Integer bigDecimalToInteger(BigDecimal duration) {
        return duration != null ? duration.intValue() : null;
    }
}
