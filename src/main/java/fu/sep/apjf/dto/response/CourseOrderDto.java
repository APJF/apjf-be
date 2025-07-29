package fu.sep.apjf.dto.response;

public record CourseOrderDto(
        String courseId,
        Long learningPathId,
        int courseOrderNumber
) {}

