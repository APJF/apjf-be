package fu.sep.apjf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ExamResultSubmitRequestDto(
        @NotBlank(message = "ID bài kiểm tra không được để trống")
        String examId,  // Exam ID vẫn là String

        @NotEmpty(message = "Danh sách câu trả lời không được để trống")
        List<ExamResultAnswerRequestDto> answers
) {
}
