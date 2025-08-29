package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass.ExamType;

public record ExamOverviewResponseDto(
        String examId,
        String title,
        String description,
        Float duration,
        int totalQuestions,
        ExamType type, // MC hoặc WRITING
        Boolean isPassed
) {}