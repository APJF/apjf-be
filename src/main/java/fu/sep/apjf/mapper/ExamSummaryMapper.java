package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.ExamSummaryDto;
import fu.sep.apjf.entity.Exam;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ExamSummaryMapper {

    // MapStruct tự động map id và title (tên giống nhau)
    ExamSummaryDto toDto(Exam exam);

    // Convert Set<Exam> to Set<ExamSummaryDto>
    default Set<ExamSummaryDto> toDtoSet(Set<Exam> exams) {
        if (exams == null) {
            return java.util.Collections.emptySet();
        }
        return exams.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
