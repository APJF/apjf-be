package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record ExamRequestDto(
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType,
        List<String> questionIds
) {
}
