package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExamResultAnswerRequestDto(
        @NotBlank(message = "ID câu hỏi không được để trống")
        String questionId,

        String userAnswer,

        String selectedOptionId
) {
}
