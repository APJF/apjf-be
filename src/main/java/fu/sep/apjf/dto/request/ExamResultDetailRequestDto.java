package fu.sep.apjf.dto.request;

public record ExamResultDetailRequestDto(
        String questionId,
        String userAnswer,
        String selectedOptionId
) {}
