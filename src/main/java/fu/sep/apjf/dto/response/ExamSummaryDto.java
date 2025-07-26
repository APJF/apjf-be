package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

/**
 * DTO đại diện cho thông tin tóm tắt của bài thi (exam)
 */
public record ExamSummaryDto(
        String id,
        String title,
        String description,
        Integer durationInMinutes,
        Integer numberOfQuestions,
        EnumClass.Status status
) {
}
