package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;

public record ExamSummaryDto(
        String id,
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType,
        LocalDateTime createdAt
) {
}
