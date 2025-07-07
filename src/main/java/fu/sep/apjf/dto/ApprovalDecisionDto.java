package fu.sep.apjf.dto;

import fu.sep.apjf.entity.ApprovalRequest.Decision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalDecisionDto(
        @NotNull(message = "Quyết định không được để trống")
        Decision decision,

        @Size(max = 255, message = "Feedback không được vượt quá 255 ký tự")
        String feedback
) {
}
