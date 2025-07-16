package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ExamSummaryDto;
import fu.sep.apjf.entity.Exam;

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
                exam.getDuration(),
                exam.getExamScopeType(),
                exam.getCreatedAt()
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
