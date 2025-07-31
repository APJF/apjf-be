package fu.sep.apjf.dto.request;

import java.util.List;

public record ExamWithQuestionsRequestDto(
        String examId,
        List<String> questionIds
) {}

