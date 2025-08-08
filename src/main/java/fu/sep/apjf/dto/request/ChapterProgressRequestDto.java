package fu.sep.apjf.dto.request;

public record ChapterProgressRequestDto(
        Long chapterId,
        Long studentId
) {}