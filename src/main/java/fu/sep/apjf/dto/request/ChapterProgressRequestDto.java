package fu.sep.apjf.dto.request;

public record ChapterProgressRequestDto(
        String chapterId,
        Long userId,
        boolean completed
) {}