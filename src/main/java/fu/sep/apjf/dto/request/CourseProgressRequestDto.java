package fu.sep.apjf.dto.request;

public record CourseProgressRequestDto(
        String courseId,
        Long userId
) {}