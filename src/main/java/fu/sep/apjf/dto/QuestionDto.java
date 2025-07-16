package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionDto(
        String id,
        String content,
        String correctAnswer,
        EnumClass.QuestionType type,
        EnumClass.QuestionScope scope,
        String explanation,
        String fileUrl,
        LocalDateTime createdAt,
        List<QuestionOptionDto> options
) {
}
