package fu.sep.cms.dto;

import fu.sep.cms.entity.Course.Level;
import fu.sep.cms.entity.Status;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/* -------- Course -------- */
public record CourseDto(
        @NotBlank String id,
        String title,
        String description,
        BigDecimal estimatedDuration,
        Level level,
        String image,
        String requirement,
        Status status
) {
}