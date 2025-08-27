package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.CourseOrderDto;
import fu.sep.apjf.dto.response.LearningPathDetailResponseDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.LearningPath;
import fu.sep.apjf.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LearningPathMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "targetLevel", source = "targetLevel", qualifiedByName = "stringToLevel")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "courses", ignore = true)
    LearningPathResponseDto toDto(LearningPath learningPath);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "targetLevel", source = "targetLevel", qualifiedByName = "stringToLevel")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "status", source = "status")
    List<LearningPathDetailResponseDto> toDetailDto(List<LearningPath> learningPath);

    // Map từ request DTO sang entity khi tạo mới
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "courseLearningPaths", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    LearningPath toEntity(LearningPathRequestDto dto);

    // Tạo entity hoàn chỉnh từ request + user
    default LearningPath toEntity(LearningPathRequestDto dto, User user) {
        LearningPath entity = toEntity(dto);
        if (entity != null) {
            entity.setUser(user);
            entity.setStatus(EnumClass.PathStatus.PENDING);
            entity.setCreatedAt(Instant.now());
            entity.setLastUpdatedAt(Instant.now());
        }
        return entity;
    }

    default LearningPathResponseDto toResponseDto(LearningPath learningPath, List<CourseOrderDto> courses) {
        return LearningPathResponseDto.of(
                learningPath.getId(),
                learningPath.getTitle(),
                learningPath.getDescription(),
                stringToLevel(learningPath.getTargetLevel()),
                learningPath.getPrimaryGoal(),
                learningPath.getFocusSkill(),
                learningPath.getStatus(),
                learningPath.getDuration(),
                learningPath.getUser().getId(),
                learningPath.getUser().getUsername(),
                learningPath.getCreatedAt(),
                learningPath.getLastUpdatedAt(),
                courses
        );
    }

    @Named("stringToLevel")
    default EnumClass.Level stringToLevel(String targetLevel) {
        if (targetLevel == null) return null;
        try {
            return EnumClass.Level.valueOf(targetLevel);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}