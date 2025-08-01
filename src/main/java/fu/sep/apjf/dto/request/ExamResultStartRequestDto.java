package fu.sep.apjf.dto.request;

public record ExamResultStartRequestDto(
        String examId,
        Long userId // Hoặc có thể lấy từ SecurityContext
) {}

