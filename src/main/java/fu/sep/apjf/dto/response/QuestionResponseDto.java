package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record QuestionResponseDto(
        String id,
        String content,
        String correctAnswer,
        EnumClass.QuestionScope scope,
        EnumClass.QuestionType type,
        List<OptionResponseDto> options
) {
}
