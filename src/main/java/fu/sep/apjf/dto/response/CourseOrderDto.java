package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CourseOrderDto(
        @NotBlank(message = "Course ID không được để trống")
        String courseId,
        Long learningPathId,
        int courseOrderNumber,
        String title,
        String description,
        BigDecimal duration,
        EnumClass.Level level
) {
}
