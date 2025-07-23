package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LearningPathDto(
        Long id,
        String title,
        String description,
        String targetLevel,
        String primaryGoal,
        String focusSkill,
        EnumClass.PathStatus status,
        BigDecimal duration,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt,
        Long userId,
        List<CourseOrderDto> courseOrder
) {}


