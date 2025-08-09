package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserAuthoritiesDto {

    @NotNull(message = "User ID không được null")
    private Long userId;

    @NotNull(message = "Authority IDs không được null")
    private List<Long> authorityIds;
}
