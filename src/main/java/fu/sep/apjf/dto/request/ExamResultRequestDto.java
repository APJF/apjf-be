package fu.sep.apjf.dto.request;

import java.time.Instant;
import java.util.List;

public record ExamResultRequestDto(
        String examId,
        Instant startedAt,
        Instant submittedAt,
        List<QuestionResultRequestDto> questionResults
) {
}