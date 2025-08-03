package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.response.OptionResponseDto;
import fu.sep.apjf.entity.Option;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    OptionResponseDto toDto(Option option);
}
