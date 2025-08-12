package fu.sep.apjf.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ChapterProgressResponseDto(
        String chapterId,
        String chapterTitle,
        boolean completed,
        LocalDateTime completedAt,
        List<UnitProgressResponseDto> unitProgresses
) {
}