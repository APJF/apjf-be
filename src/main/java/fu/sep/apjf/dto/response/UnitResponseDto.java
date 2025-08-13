package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.Set;

public record UnitResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String chapterId,
        String prerequisiteUnitId
) {
}
