package fu.sep.cms.dto;

import fu.sep.cms.entity.Status;
import jakarta.validation.constraints.NotBlank;

public record UnitDto(@NotBlank String id, String title, String description, Status status, String chapterId) {
}