package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.ExamOverviewResponseDto;
import fu.sep.apjf.entity.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamOverviewMapper {

    @Mapping(target = "examId", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "totalQuestions", expression = "java(exam.getQuestions() != null ? exam.getQuestions().size() : 0)")
    @Mapping(target = "type", source = "type")
    ExamOverviewResponseDto toDto(Exam exam);
}