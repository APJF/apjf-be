package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;

public record ExamSummaryRequestDto(
        String id,
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType
) {
}
