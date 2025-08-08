package fu.sep.apjf.dto.request;

public record LearningPathProgressRequestDto(
        Long learningPathId,
        Long userId,
        boolean completed
) {
}
