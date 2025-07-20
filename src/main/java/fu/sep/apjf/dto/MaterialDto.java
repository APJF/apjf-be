package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

public record MaterialDto(
        String id,
        String description,
        String fileUrl,
        EnumClass.MaterialType type,
        String unitId
) {
}
