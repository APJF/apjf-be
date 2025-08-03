package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import jakarta.validation.constraints.NotNull;

public record MaterialRequestDto(
        String id,

        String fileUrl,

        @NotNull(message = "Loại tài liệu không được để trống")
        EnumClass.MaterialType type,

        String script,

        String translation,

        @NotNull(message = "ID unit không được để trống")
        String unitId
) {
}
