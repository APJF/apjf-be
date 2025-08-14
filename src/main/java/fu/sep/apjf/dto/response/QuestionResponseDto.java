package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;
import java.util.List;

public record QuestionResponseDto(
        String id,
        String content,
        EnumClass.QuestionScope scope,
        EnumClass.QuestionType type,
        String fileUrl,
        Instant createdAt,
        List<OptionExamResponseDto> options,
        List<String> unitIds
) {
}