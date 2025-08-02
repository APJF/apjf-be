package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record CourseRequestDto(

        @NotNull(message ="ID khoá học không được để trống")
        String id,
        @NotBlank(message = "Tiêu đề khóa học không được để trống")
        @Size(min = 1, max = 255, message = "Tiêu đề khóa học phải từ 1 đến 255 ký tự")
        String title,
        @NotBlank(message = "Mô tả khóa học không được để trống")
        @Size(min = 1, max = 2000, message = "Mô tả khóa học phải từ 1 đến 2000 ký tự")
        String description,
        @NotNull(message = "Thời gian khóa học không được để trống")
        BigDecimal duration,
        @NotNull(message = "Cấp độ khóa học không được để trống")
        EnumClass.Level level,
        String image,
        String requirement,
        EnumClass.Status status,
        String prerequisiteCourseId,
        Set<String> topicIds,
        Set<String> examIds
) {
}