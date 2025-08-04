package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.QuestionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Question;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OptionMapper.class})
public interface QuestionMapper {

    @Mapping(target = "options", ignore = true)
    QuestionResponseDto toDto(Question entity);

    @Mapping(target = "exams", ignore = true)
    @Mapping(target = "examResultDetails", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "units", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Question toEntity(QuestionRequestDto dto);

    @ObjectFactory
    default QuestionResponseDto toDtoWithOptions(Question question, @Context OptionMapper optionMapper) {
        QuestionResponseDto dto = toDto(question);
        List<OptionResponseDto> optionDtos = optionMapper.toDtoList(question.getOptions());
        return new QuestionResponseDto(
                dto.id(),
                dto.content(),
                dto.scope(),
                dto.type(),
                dto.explanation(),
                dto.fileUrl(),
                dto.createdAt(),
                optionDtos
        );
    }
}
