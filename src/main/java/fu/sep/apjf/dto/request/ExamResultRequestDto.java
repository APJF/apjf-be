package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;
//student gửi chi tiết 1 câu trong bài thi
public record ExamResultRequestDto(
        @NotBlank(message = "ID câu hỏi không được để trống")
        String questionId,

        String userAnswer,

        String selectedOptionId
) {
}
