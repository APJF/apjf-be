package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.QuestionResponseDto;
import fu.sep.apjf.entity.Question;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {OptionMapper.class})
public interface QuestionMapper {

    QuestionResponseDto toDto(Question question);
}
