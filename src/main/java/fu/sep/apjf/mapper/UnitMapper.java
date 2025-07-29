package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Unit;

import java.util.Set;
import java.util.stream.Collectors;

public final class UnitMapper {

    private UnitMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Unit entity to UnitResponseDto (used for responses to clients)
     */
    public static UnitResponseDto toDto(Unit unit) {
        if (unit == null) {
            return null;
        }

        return new UnitResponseDto(
                unit.getId(),
                unit.getTitle(),
                unit.getDescription(),
                unit.getStatus(),
                unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null
        );
    }

    /**
     * Convert Unit entity to UnitRequestDto (used for testing or internal purposes)
     */
    public static UnitRequestDto toRequestDto(Unit unit) {
        if (unit == null) {
            return null;
        }

        // Get exam IDs for this unit
        Set<String> examIds = unit.getExams() != null ?
            unit.getExams().stream()
                .map(exam -> exam.getId())
                .collect(Collectors.toSet()) :
            null;

        return new UnitRequestDto(
                unit.getId(),
                unit.getTitle(),
                unit.getDescription(),
                unit.getStatus(),
                unit.getChapter().getId(),
                unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null,
                examIds
        );
    }

    public static Unit toEntity(UnitRequestDto unitDto) {
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

    public static Unit toEntity(UnitRequestDto unitDto, Chapter chapter) {
        Unit unit = toEntity(unitDto);
        if (unit != null && chapter != null) {
            unit.setChapter(chapter);
        }
        return unit;
    }
}
