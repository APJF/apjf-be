package fu.sep.apjf.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record ExamResultRequestDto(
        String examId,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        List<QuestionResultRequestDto> questionResults
) {}
