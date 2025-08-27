package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record MaterialDetailResponseDto(
        String id,
        String fileUrl,
        EnumClass.MaterialType type,
        String script,
        String translation,
        List<ExamOverviewResponseDto> exams
) {
}