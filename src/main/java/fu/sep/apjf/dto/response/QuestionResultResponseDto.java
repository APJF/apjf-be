package fu.sep.apjf.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuestionResultResponseDto(
        String questionId,
        String questionContent,
        String explanation,
        String selectedOptionId, // dùng cho MC
        String userAnswer,       // dùng cho Writing
        boolean isCorrect,
        List<OptionResponseDto> options
) {}
