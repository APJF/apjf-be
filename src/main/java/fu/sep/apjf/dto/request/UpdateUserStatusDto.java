package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusDto {

    @NotNull(message = "User ID không được null")
    private Long userId;

    @NotNull(message = "Enabled status không được null")
    private Boolean enabled;

    private String reason; // Lý do ban/unban
}
