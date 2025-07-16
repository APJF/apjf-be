package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.UnitDto;
import fu.sep.apjf.entity.Chapter;
import fu.sep.apjf.entity.Unit;

public final class UnitMapper {

    private UnitMapper() {
        // Private constructor to prevent instantiation
    }

    public static UnitDto toDto(Unit unit) {
        if (unit == null) {
            return null;
        }

        return new UnitDto(
                unit.getId(),
                unit.getTitle(),
                unit.getDescription(),
                unit.getStatus(),
                unit.getChapter().getId(),
                unit.getPrerequisiteUnit() != null ? unit.getPrerequisiteUnit().getId() : null
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
