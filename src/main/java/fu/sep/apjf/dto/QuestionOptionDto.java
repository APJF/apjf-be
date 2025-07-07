package fu.sep.apjf.dto;

import lombok.Data;

@Data
public class QuestionOptionDto {
    private String id;
    private String content;
    private Boolean isCorrect;
}
