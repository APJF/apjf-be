package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.util.List;

public record UnitDetailWithExamResponseDto(
        String id,
        String title,
        String description,
        EnumClass.Status status,
        String chapterId,
        String prerequisiteUnitId,
        boolean isComplete,
        List<MaterialResponseDto> materials,
        List<ExamOverviewResponseDto> exams
) {
}
