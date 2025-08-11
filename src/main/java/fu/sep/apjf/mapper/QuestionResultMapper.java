package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.QuestionResultRequestDto;
import fu.sep.apjf.dto.response.QuestionResultResponseDto;
import fu.sep.apjf.entity.ExamResultDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuestionResultMapper {

    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "question.content", target = "questionContent")
    @Mapping(source = "question.explanation", target = "explanation")
    @Mapping(source = "selectedOption.id", target = "selectedOptionId")
    @Mapping(source = "userAnswer", target = "userAnswer")
    @Mapping(source = "isCorrect", target = "isCorrect")
    QuestionResultResponseDto toDto(ExamResultDetail detail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isCorrect", ignore = true)
    @Mapping(target = "examResult", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "selectedOption", ignore = true)
    ExamResultDetail toEntity(QuestionResultRequestDto dto);
}