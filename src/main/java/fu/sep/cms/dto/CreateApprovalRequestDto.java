package fu.sep.cms.dto;

import fu.sep.cms.entity.ApprovalRequest.RequestType;
import fu.sep.cms.entity.ApprovalRequest.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating new approval requests by staff
 */
public record CreateApprovalRequestDto(
        @NotNull(message = "Loại đối tượng không được để trống")
        TargetType targetType,

        @NotBlank(message = "ID đối tượng không được để trống")
        String targetId,

        @NotNull(message = "Loại yêu cầu không được để trống")
        RequestType requestType,

        @Size(max = 255, message = "Lý do yêu cầu không được vượt quá 255 ký tự")
        String reason
) {
}
