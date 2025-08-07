package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;
import java.time.LocalDateTime;
import java.util.List;

public record QuestionResponseDto(
        String id,
        String content,
        EnumClass.QuestionScope scope,
        EnumClass.QuestionType type,
        String explanation,
        String fileUrl,
        LocalDateTime createdAt,
        List<OptionResponseDto> options,
        List<String> unitIds
) {}
