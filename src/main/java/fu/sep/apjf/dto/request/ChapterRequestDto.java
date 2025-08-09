package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.validation.ValidId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChapterRequestDto(
        @NotBlank(message = "ID chương không được để trống")
        @ValidId(message = "ID chương chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
        String id,
        @NotBlank(message = "Tiêu đề chương không được để trống")
        @Size(min = 1, max = 255, message = "Tiêu đề chương phải từ 1 đến 255 ký tự")
        String title,
        @NotBlank(message = "Mô tả chương không được để trống")
        @Size(min = 1, max = 2000, message = "Mô tả chương phải từ 1 đến 2000 ký tự")
        String description,
        EnumClass.Status status,
        @NotBlank(message = "ID khóa học không được để trống")
        @ValidId(message = "ID khóa học chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
        String courseId,
        @ValidId(message = "ID chương tiên quyết chỉ được chứa chữ cái, số, dấu gạch ngang (-) và dấu gạch dưới (_)")
        String prerequisiteChapterId
) {
}