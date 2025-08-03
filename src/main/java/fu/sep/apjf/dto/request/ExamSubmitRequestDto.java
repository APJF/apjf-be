package fu.sep.apjf.dto.request;

import java.util.List;
//Student gửi kết quả bài thi
public record ExamSubmitRequestDto(
        String examId,
        List<ExamResultRequestDto> answers
) {}

