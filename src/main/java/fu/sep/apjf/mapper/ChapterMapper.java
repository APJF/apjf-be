package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.ChapterRequestDto;
import fu.sep.apjf.dto.response.ChapterResponseDto;
import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ChapterMapper {

    private ChapterMapper() {
        // Private constructor to prevent instantiation
    }

    public static ChapterResponseDto toResponseDto(Chapter chapter) {
        if (chapter == null) {
            return null;
        }

        // Get exams for this chapter
        Set<ExamSummaryDto> chapterExams = chapter.getExams().stream()
                .map(ExamSummaryMapper::toDto)
                .collect(Collectors.toSet());

        return new ChapterResponseDto(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getDescription(),
                chapter.getStatus(),
                chapter.getCourse() != null ? chapter.getCourse().getId() : null,
                chapter.getPrerequisiteChapter() != null ? chapter.getPrerequisiteChapter().getId() : null,
                chapterExams
        );
    }

    public static Chapter toEntity(ChapterRequestDto chapterDto) {
        if (chapterDto == null) {
            return null;
        }

        return Chapter.builder()
                .id(chapterDto.id())
                .title(chapterDto.title())
                .description(chapterDto.description())
                .status(chapterDto.status())
                .build();
    }

    public static Chapter toEntity(ChapterRequestDto chapterDto, Course course) {
        Chapter chapter = toEntity(chapterDto);
        if (chapter != null && course != null) {
            chapter.setCourse(course);
        }
        return chapter;
    }

    public static List<ChapterResponseDto> toResponseDtoList(List<Chapter> chapters) {
        if (chapters == null) {
            return Collections.emptyList();
        }
        return chapters.stream()
                .map(ChapterMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
