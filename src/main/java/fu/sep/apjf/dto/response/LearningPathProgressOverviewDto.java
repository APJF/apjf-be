package fu.sep.apjf.dto.response;

public record LearningPathProgressOverviewDto(
        String learningPathId,
        String learningPathTitle,
        int totalUnits,
        int completedUnits,
        double progressPercentage
) {}
