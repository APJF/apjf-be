package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDto {
    private String id;
    private String title;
    private String description;
    private Integer duration;
    private EnumClass.ExamScopeType examScopeType;
    private LocalDateTime createdAt;
    private List<QuestionDto> questions;
    private int totalQuestions;
}
