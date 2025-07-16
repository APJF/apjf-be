package fu.sep.apjf.dto;

import fu.sep.apjf.entity.ApprovalRequest.RequestType;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
