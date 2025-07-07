package fu.sep.apjf.dto;

import lombok.Data;

@Data
public class AnswerSubmissionDto {
    private String questionId;
    private String userAnswer;
    private String selectedOptionId;
}
