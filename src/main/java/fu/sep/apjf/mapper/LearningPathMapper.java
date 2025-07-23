package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.LearningPathDto;
import fu.sep.apjf.entity.LearningPath;
import fu.sep.apjf.entity.User;

import java.util.stream.Collectors;

public class LearningPathMapper {

    private LearningPathMapper() {}

    public static LearningPathDto toDto(LearningPath entity) {
        return new LearningPathDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getTargetLevel(),
                entity.getPrimaryGoal(),
                entity.getFocusSkill(),
                entity.getStatus(),
                entity.getDuration(),
                entity.getCreatedAt(),
                entity.getLastUpdatedAt(),
                entity.getUser().getId(),
                entity.getCourseLearningPaths().stream()
                        .map(CourseLearningPathMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    public static LearningPath toEntity(LearningPathDto dto, User user) {
        return LearningPath.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .targetLevel(dto.targetLevel())
                .primaryGoal(dto.primaryGoal())
                .focusSkill(dto.focusSkill())
                .status(dto.status())
                .duration(dto.duration())
                .createdAt(dto.createdAt())
                .lastUpdatedAt(dto.lastUpdatedAt())
                .user(user)
                .build();
    }
}
