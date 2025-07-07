package fu.sep.apjf.dto;

import lombok.Data;

@Data
public class ExamResultAnswerDto {
    private String id;
    private String userAnswer;
    private Boolean isCorrect;
    private String questionId;
    private String questionContent;
    private String selectedOptionId;
    private String correctAnswer;
}
