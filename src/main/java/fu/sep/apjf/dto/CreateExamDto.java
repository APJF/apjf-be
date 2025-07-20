package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record CreateExamDto(
        String title,
        String description,
        Double duration,
        EnumClass.ExamScopeType examScopeType,
        List<String> questionIds
) {
}
