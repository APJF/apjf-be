package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

public record ChapterResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String courseId,
        String prerequisiteChapterId
) {
}
