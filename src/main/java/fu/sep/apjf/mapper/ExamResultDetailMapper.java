package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.QuestionResultRequestDto;
import fu.sep.apjf.dto.response.QuestionResultResponseDto;
import fu.sep.apjf.entity.ExamResultDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamResultDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isCorrect", ignore = true)
    @Mapping(target = "examResult", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "selectedOption", ignore = true)
    ExamResultDetail toEntity(QuestionResultRequestDto dto);

    @Mapping(target = "questionId", ignore = true)
    @Mapping(target = "questionContent", ignore = true)
    @Mapping(target = "explanation", ignore = true)
    @Mapping(target = "selectedOptionId", ignore = true)
    QuestionResultResponseDto toDto(ExamResultDetail detail);
}
