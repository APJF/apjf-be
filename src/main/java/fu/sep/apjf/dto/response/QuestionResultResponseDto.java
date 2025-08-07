package fu.sep.apjf.dto.response;

import lombok.Builder;

@Builder
public record QuestionResultResponseDto(
        String questionId,
        String questionContent,
        String explanation,
        String selectedOptionId, // dùng cho MC
        String userAnswer,       // dùng cho Writing
        boolean isCorrect
) {}
