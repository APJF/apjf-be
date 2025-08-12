
package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.UnitProgressRequestDto;
import fu.sep.apjf.dto.response.UnitProgressResponseDto;
import fu.sep.apjf.entity.Unit;
import fu.sep.apjf.entity.UnitProgress;
import fu.sep.apjf.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UnitProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "completed", source = "dto.completed")
    @Mapping(target = "completedAt", expression = "java(dto.completed() ? java.time.LocalDateTime.now() : null)")
    UnitProgress toEntity(UnitProgressRequestDto dto, Unit unit, User user);

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitTitle", source = "unit.title")
    UnitProgressResponseDto toDto(UnitProgress entity);
}
