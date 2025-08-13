package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass.Status;
import fu.sep.apjf.validation.ValidId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO đại diện cho thông tin yêu cầu về bài học (Unit)
 */
public record UnitRequestDto(
        @ValidId(message = "ID bài học chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
        String id,
        @NotBlank(message = "Tiêu đề bài học không được để trống")
        @Size(min = 1, max = 255, message = "Tiêu đề bài học phải từ 1 đến 255 ký tự")
        String title,
        @NotBlank(message = "Mô tả bài học không được để trống")
        @Size(min = 1, max = 2000, message = "Mô tả bài học phải từ 1 đến 2000 ký tự")
        String description,
        Status status,
        @NotBlank(message = "ID chương không được để trống")
        @ValidId(message = "ID chương chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
        String chapterId,
        @ValidId(message = "ID bài học tiên quyết chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
        String prerequisiteUnitId
) {
}