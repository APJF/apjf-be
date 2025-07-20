package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ChapterDto;
import fu.sep.apjf.dto.ExamSummaryDto;
import fu.sep.apjf.dto.UnitDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Course;

import java.util.Set;
import java.util.stream.Collectors;

public final class ChapterMapper {

    private ChapterMapper() {
        // Private constructor to prevent instantiation
    }

    public static ChapterDto toDto(Chapter chapter) {
        if (chapter == null) {
            return null;
        }

        // Map units and include their exams
        Set<UnitDto> unitDtos = chapter.getUnits().stream()
                .map(unit -> {
                    // Get exams for this unit
                    Set<ExamSummaryDto> unitExams = unit.getExams().stream()
                            .map(ExamSummaryMapper::toDto)
                            .collect(Collectors.toSet());

                    return new UnitDto(
                            unit.getId(),
                            unit.getTitle(),
                            unit.getDescription(),
                            unit.getStatus(),
                            chapter.getId(),
                            unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null,
                            unitExams
                    );
                })
                .collect(Collectors.toSet());

        // Get exams for this chapter
        Set<ExamSummaryDto> chapterExams = chapter.getExams().stream()
                .map(ExamSummaryMapper::toDto)
                .collect(Collectors.toSet());

        return new ChapterDto(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getDescription(),
                chapter.getStatus(),
                chapter.getCourse().getId(),
                chapter.getPrerequisiteChapter() != null ? chapter.getPrerequisiteChapter().getId() : null,
                chapterExams,
                unitDtos

        );
    }

    public static Chapter toEntity(ChapterDto chapterDto) {
        if (chapterDto == null) {
            return null;
        }

        Chapter chapter = new Chapter();
        chapter.setId(chapterDto.id());
        chapter.setTitle(chapterDto.title());
        chapter.setDescription(chapterDto.description());
        chapter.setStatus(chapterDto.status());

        return chapter;
    }

    public static Chapter toEntity(ChapterDto chapterDto, Course course) {
        Chapter chapter = toEntity(chapterDto);
        if (chapter != null && course != null) {
            chapter.setCourse(course);
        }
        return chapter;
    }
}
