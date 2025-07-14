package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamResultDto {
    private String id;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Float score;
    private EnumClass.ExamStatus status;
    private String userId;
    private String examId;
    private String examTitle;
    private List<ExamResultDetailDto> answers;
    private int totalQuestions;
    private int correctAnswers;
}
