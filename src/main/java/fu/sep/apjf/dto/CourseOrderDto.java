package fu.sep.apjf.dto;

public record CourseOrderDto(
        String courseId,
        Long learningPathId,
        int courseOrderNumber
) {}

