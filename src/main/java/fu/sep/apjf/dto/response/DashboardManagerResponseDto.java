package fu.sep.apjf.dto.response;

import java.util.List;

public record DashboardManagerResponseDto (
        int totalCourse,
        int totalActiveCourse,
        int totalInactiveCourse,
        int totalChapter,
        int totalActiveChapter,
        int totalInactiveChapter,
        int totalUnit,
        int totalActiveUnit,
        int totalInactiveUnit,
        int totalMaterial,
        int totalExam,
        List<CourseProgressPercentResponseDto> coursesTotalCompletedPercent,
        List<CourseTotalEnrollResponseDto> courseMonthlyActivity
){
}
