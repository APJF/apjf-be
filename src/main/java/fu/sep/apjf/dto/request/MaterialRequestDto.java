package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaterialRequestDto(
        String id,

        @NotBlank(message = "Mô tả không được để trống")
        String description,

        String fileUrl,

        @NotNull(message = "Loại tài liệu không được để trống")
        EnumClass.MaterialType type
) {
}
