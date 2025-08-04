package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.OptionRequestDto;
import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.entity.Option;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    Option toEntity(OptionRequestDto dto);

    OptionResponseDto toResponseDto(Option option);

    List<OptionResponseDto> toDtoList(List<Option> options); // thêm hàm này nếu bạn cần mapping list
}
