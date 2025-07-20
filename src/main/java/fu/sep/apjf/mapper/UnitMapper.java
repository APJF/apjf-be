package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ExamSummaryDto;
import fu.sep.apjf.dto.UnitDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Unit;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class UnitMapper {

    private UnitMapper() {
        // Private constructor to prevent instantiation
    }

    public static UnitDto toDto(Unit unit) {
        if (unit == null) {
            return null;
        }

        // Get exams for this unit
        Set<ExamSummaryDto> examDtos = unit.getExams().stream()
                .map(ExamSummaryMapper::toDto)
                .collect(Collectors.toSet());

        return new UnitDto(
                unit.getId(),
                unit.getTitle(),
                unit.getDescription(),
                unit.getStatus(),
                unit.getChapter().getId(),
                unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null,
                examDtos
        );
    }

    public static Unit toEntity(UnitDto unitDto) {
        if (unitDto == null) {
            return null;
        }

        Unit unit = new Unit();
        unit.setId(unitDto.id());
        unit.setTitle(unitDto.title());
        unit.setDescription(unitDto.description());
        unit.setStatus(unitDto.status());

        return unit;
    }

    public static Unit toEntity(UnitDto unitDto, Chapter chapter) {
        Unit unit = toEntity(unitDto);
        if (unit != null && chapter != null) {
            unit.setChapter(chapter);
        }
        return unit;
    }
}
