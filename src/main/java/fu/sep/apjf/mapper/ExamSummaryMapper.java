package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.entity.Exam;
import fu.sep.apjf.entity.EnumClass;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class ExamSummaryMapper {

    private ExamSummaryMapper() {
        // Private constructor to prevent instantiation
    }

    public static ExamSummaryDto toDto(Exam exam) {
        if (exam == null) {
            return null;
        }

        return new ExamSummaryDto(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getDuration() != null ? exam.getDuration().intValue() : null,
                exam.getQuestions() != null ? exam.getQuestions().size() : 0,
                EnumClass.Status.DRAFT // Giá trị mặc định hoặc lấy từ exam nếu có
        );
    }

    public static Set<ExamSummaryDto> toDtoSet(Set<Exam> exams) {
        if (exams == null) {
            return Collections.emptySet();
        }

        return exams.stream()
                .map(ExamSummaryMapper::toDto)
                .collect(Collectors.toSet());
    }
}
