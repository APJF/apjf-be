package fu.sep.apjf.dto.request;

public record ExamResultStartRequestDto(
        String examId,  // ← Exam ID vẫn là String
        Long userId // Hoặc có thể lấy từ SecurityContext
) {}
