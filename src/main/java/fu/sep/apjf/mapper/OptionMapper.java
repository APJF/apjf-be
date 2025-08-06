package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.entity.Option;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    OptionResponseDto toDto(Option option);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "selectedByAnswers", ignore = true)
    Option toEntity(OptionRequestDto dto);
}
