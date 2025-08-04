package fu.sep.apjf.dto.response;

import lombok.Builder;

@Builder
public record QuestionResultResponseDto(
        String questionId,
        String questionContent,
        String explanation,
        String selectedOptionId,
        boolean isCorrect
) {}
