package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import java.util.List;

public record QuestionRequestDto(
        String id,
        String content,
        EnumClass.QuestionScope scope,
        EnumClass.QuestionType type,
        String explanation,
        String fileUrl,
        List<OptionRequestDto> options,
        List<String> unitIds
) {}
