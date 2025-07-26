package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO đại diện cho chủ đề (topic) của khóa học
 */
public record TopicDto(
        Integer id,
        @NotBlank(message = "Tên chủ đề không được để trống")
        String name
) {
}
