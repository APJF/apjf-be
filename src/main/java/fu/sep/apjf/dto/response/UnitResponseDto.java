package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

public record UnitResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String prerequisiteUnitId) {
}
