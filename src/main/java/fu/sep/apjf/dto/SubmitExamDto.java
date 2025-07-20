package fu.sep.apjf.dto;

import java.util.List;

public record SubmitExamDto(
        String examId,
        List<AnswerSubmissionDto> answers
) {

}
