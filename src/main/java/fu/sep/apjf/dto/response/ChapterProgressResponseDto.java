package fu.sep.apjf.dto.response;

import java.time.Instant;
import java.util.Set;

public record ChapterProgressResponseDto(
        String chapterId,
        String chapterTitle,
        boolean completed,
        Instant completedAt,
        Set<UnitProgressResponseDto> unitProgresses
) {
}