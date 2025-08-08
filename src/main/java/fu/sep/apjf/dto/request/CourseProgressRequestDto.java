package fu.sep.apjf.dto.request;

public record CourseProgressRequestDto(
        Long courseId,
        Long studentId
) {}