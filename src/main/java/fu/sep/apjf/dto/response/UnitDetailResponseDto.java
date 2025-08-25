package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record UnitDetailResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String chapterId,
        String prerequisiteUnitId,
        List<MaterialResponseDto> materials
) {
}
