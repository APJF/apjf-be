package fu.sep.apjf.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequestDto {
    @NotNull
    private Long userId;

    @NotNull
    private String courseId;

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;
}
