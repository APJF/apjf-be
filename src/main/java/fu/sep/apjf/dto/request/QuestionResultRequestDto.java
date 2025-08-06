package fu.sep.apjf.dto.request;

import lombok.Builder;

@Builder
public record QuestionResultRequestDto(
        String questionId,
        String selectedOptionId, // nếu là câu MC
        String userAnswer        // nếu là câu writing
) {}
