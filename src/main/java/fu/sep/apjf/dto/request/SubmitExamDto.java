package fu.sep.apjf.dto.request;

import java.util.List;

public record SubmitExamDto(
        String examId,
        List<ExamResultAnswerRequestDto> answers
) {

}
