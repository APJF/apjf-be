package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    @Mapping(target = "question", ignore = true)
    @Mapping(target = "selectedByAnswers", ignore = true)
    Option toEntity(OptionRequestDto dto);

    List<OptionResponseDto> toDtoList(List<Option> options); // thêm hàm này nếu bạn cần mapping list
}
