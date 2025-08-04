package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.Set;

/**
 * DTO đại diện cho phản hồi thông tin chương
 */
public record ChapterResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String courseId,
        String prerequisiteChapterId,
        Set<UnitResponseDto> units,
        Set<ExamOverviewResponseDto> exams
) {
}
