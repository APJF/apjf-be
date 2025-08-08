package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.UnitProgressRequestDto;
import fu.sep.apjf.dto.response.UnitProgressResponseDto;
import fu.sep.apjf.entity.UnitProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitProgressMapper {

    @Mapping(target = "unitId", source = "id.unitId")
    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "unitTitle", source = "unit.title")
    UnitProgressResponseDto toDto(UnitProgress entity);

    @Mapping(target = "id.unitId", source = "unitId")
    @Mapping(target = "id.userId", source = "userId")
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "user", ignore = true)
    UnitProgress toEntity(UnitProgressRequestDto dto);
}