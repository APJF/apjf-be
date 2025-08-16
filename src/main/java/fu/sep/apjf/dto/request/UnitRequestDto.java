package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO đại diện cho thông tin yêu cầu về bài học (Unit)
 */
public record UnitRequestDto(
        @NotNull(message = "ID bài học không được để trống")
        String id,
        @NotBlank(message = "Tiêu đề bài học không được để trống")
        @Size(min = 1, max = 255, message = "Tiêu đề bài học phải từ 1 đến 255 ký tự")
        String title,
        @NotBlank(message = "Mô tả bài học không được để trống")
        @Size(min = 1, max = 2000, message = "Mô tả bài học phải từ 1 đến 2000 ký tự")
        String description,
        Status status,
        @NotBlank(message = "ID chương không được để trống")
        @NotNull(message = "ID chương không được để trống")
        String chapterId,
        @NotNull(message = "ID bài học tiên quyết không được để trống")
        String prerequisiteUnitId
) {
}