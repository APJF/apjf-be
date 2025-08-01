package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import java.time.LocalDateTime;

public record ExamResultStatusDto(
        boolean hasStarted,          // Đã bắt đầu làm bài chưa
        boolean hasCompleted,        // Đã hoàn thành bài thi chưa
        EnumClass.ExamStatus status, // Trạng thái: IN_PROGRESS, PASSED, FAILED
        LocalDateTime startedAt,     // Thời gian bắt đầu (nếu có)
        LocalDateTime submittedAt,   // Thời gian nộp bài (nếu có)
        Float score,                 // Điểm số (nếu đã hoàn thành)
        String examResultId          // ID của exam result (nếu có)
) {
    // Constructor cho trường hợp chưa làm bài
    public static ExamResultStatusDto notStarted() {
        return new ExamResultStatusDto(false, false, null, null, null, null, null);
    }

    // Constructor cho trường hợp đang làm bài
    public static ExamResultStatusDto inProgress(String examResultId, LocalDateTime startedAt) {
        return new ExamResultStatusDto(true, false, EnumClass.ExamStatus.IN_PROGRESS, startedAt, null, null, examResultId);
    }

    // Constructor cho trường hợp đã hoàn thành
    public static ExamResultStatusDto completed(String examResultId, LocalDateTime startedAt, LocalDateTime submittedAt,
                                                EnumClass.ExamStatus status, Float score) {
        return new ExamResultStatusDto(true, true, status, startedAt, submittedAt, score, examResultId);
    }
}
