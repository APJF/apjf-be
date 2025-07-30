package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

public record ExamSummaryRequestDto(
        String id,
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType
) {
}
