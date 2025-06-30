package fu.sep.cms.dto;

import fu.sep.cms.entity.Status;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record ChapterDto(
        @NotBlank String id,
        String title,
        String description,
        Status status,
        String courseId,
        Set<UnitDto> units
) {
}