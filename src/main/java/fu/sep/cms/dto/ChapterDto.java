package fu.sep.cms.dto;

import fu.sep.cms.entity.EnumClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ChapterDto(
        String id,
        @NotBlank(message = "Tiêu đề chương không được để trống")
        @Size(min = 1, max = 255, message = "Tiêu đề chương phải từ 1 đến 255 ký tự")
        String title,
        @NotBlank(message = "Mô tả chương không được để trống")
        @Size(min = 1, max = 2000, message = "Mô tả chương phải từ 1 đến 2000 ký tự")
        String description,
        EnumClass.Status status,
        @NotBlank(message = "ID khóa học không được để trống")
        String courseId,
        String prerequisiteChapterId,
        Set<UnitDto> units
) {
}