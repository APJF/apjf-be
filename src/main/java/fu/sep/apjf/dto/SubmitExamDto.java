package fu.sep.apjf.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitExamDto {
    private String examResultId;
    private List<AnswerSubmissionDto> answers;
}
