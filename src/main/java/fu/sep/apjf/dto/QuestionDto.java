package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDto {
    private String id;
    private String content;
    private String correctAnswer;
    private EnumClass.QuestionType type;
    private String explanation;
    private String fileUrl;
    private LocalDateTime createdAt;
    private List<QuestionOptionDto> options;
}
