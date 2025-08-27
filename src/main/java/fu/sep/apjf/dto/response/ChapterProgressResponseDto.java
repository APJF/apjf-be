package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;
import java.util.Set;

public record ChapterProgressResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String courseId,
        String prerequisiteChapterId,
        boolean isCompleted,
        float percent
) {
}