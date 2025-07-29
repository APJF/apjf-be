package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

public record MaterialResponseDto(
        String id,
        String description,
        String fileUrl,
        EnumClass.MaterialType type
) {
}
