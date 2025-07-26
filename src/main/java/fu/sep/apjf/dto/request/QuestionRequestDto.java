package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record QuestionRequestDto(
        String id,
        String content,
        String correctAnswer,
        EnumClass.QuestionScope scope,
        EnumClass.QuestionType type,
        List<QuestionOptionRequestDto> options
) {
}
