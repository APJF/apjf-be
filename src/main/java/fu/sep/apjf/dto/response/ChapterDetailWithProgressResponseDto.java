package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record ChapterDetailWithProgressResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String courseId,
        String prerequisiteChapterId,
        List<UnitDetailWithExamResponseDto> units
) {
}
