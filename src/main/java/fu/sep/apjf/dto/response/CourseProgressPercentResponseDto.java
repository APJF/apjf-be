package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

public record CourseProgressPercentResponseDto (
        String id,
        String title,
        EnumClass.Level level,
        int totalCompleted,
        int totalEnrolled,
        float percent
){
}
