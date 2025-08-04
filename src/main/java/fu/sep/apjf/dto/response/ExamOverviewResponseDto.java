package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass.ExamType;

public record ExamOverviewResponseDto(
        String examId,
        String title,
        String description,
        double durationMinutes,
        int totalQuestions,
        ExamType type // MC hoặc WRITING
) {}
