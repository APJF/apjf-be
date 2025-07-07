package fu.sep.apjf.dto;

import java.util.Set;

public record CourseDetailDto(
        CourseDto course,
        Set<ChapterDto> chapters
) {
}